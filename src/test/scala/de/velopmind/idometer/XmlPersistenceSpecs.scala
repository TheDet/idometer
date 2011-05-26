/*
 * Copyright 2011 Dirk Detering <mailtodet@googlemail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.velopmind.idometer.xml

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import java.util.Date

import de.velopmind.idometer._

@RunWith( classOf[JUnitRunner])
class PersistenceSpec  extends FlatSpec with ShouldMatchers  {
    import de.velopmind.idometer.Time._
    import TimestampTestUtil._    

    "A persistence" should "marshal and unmarshal an activity" in {
        def validate(act:Activity) {
          val persist = new Persistence()
          val xml = persist.activityToXml(act)
          val actagain = persist.xmlToActivity(xml)

          act should equal (actagain)   
        }

        validate( Activity(1, new Date(), Some(new Date()), "actOne") )
        validate( Activity(1, new Date(), None, "actOne") )
    }

    it should "marshal and unmarshal a task" in {
        import de.velopmind.idometer.Time._
        def validate(task: Task) {
          val persist = new Persistence()
          val xml = persist.taskToXml(task)
          val taskagain = persist.xmlToTask(xml)

          task should equal (taskagain)   
        }
        
        validate( Task(1, "one", "one's description",
                        Duration(2 h),  Duration(0), // will not be restored: Duration(1 h),  
                        Nil, true ) ) 
    }

  
    it should "unmarshal a sequence of activities" in {
         def  makeActivityXml(tid:Int, start:Date, desc:String) = <activity>
              <taskid>{tid.toString}</taskid>
              <start>{start.getTime()}</start>
              <stop />
              <desc>{desc}</desc>
              </activity> 
         implicit val testdate = "20110524"

         val xml = <root>
                  {makeActivityXml(1, createdate("08:32:00"), "Uno")}
                  {makeActivityXml(2, createdate("09:45:00"), "Dos")}
                  {makeActivityXml(2, createdate("10:15:00"), "Tres")}
                  </root>
         
         val persist = new Persistence()
         val activities = persist.xmlToActivities(xml)
         activities should have size (3)
         activities should contain (Activity(1, createdate("08:32:00"), None, "Uno" ) )
         activities should contain (Activity(2, createdate("09:45:00"), None, "Dos" ) )
         activities should contain (Activity(2, createdate("10:15:00"), None, "Tres" ) )
         
    }

    it should "unmarshal a sequence of tasks" in {
         def makeTaskXml(tid:Int, title:String, estimated:Duration, finished:Boolean, desc:String) = 
             <task id={tid.toString}>
             <title>{title}</title>
             <descr>{desc}</descr>
             <estimated>{estimated.toLong}</estimated>
             <finished>{finished}</finished>
             </task>     

         val xml = <root>
                  {makeTaskXml(1, "one", Duration(987654329), true, "Uno")}
                  {makeTaskXml(2, "two", Duration(987654328), true, "Dos")}
                  {makeTaskXml(3, "three", Duration(987654327), false, "Tres")}
                  </root>
         
         val persist = new Persistence()
         val tasks = persist.xmlToTasks(xml)
         tasks should have size (3)
         tasks should contain key (1)
         tasks should contain value (Task(1, "one",   "Uno",  Duration(987654329), Duration(0), Nil, true))
         tasks should contain key (2)
         tasks should contain value (Task(2, "two",   "Dos",  Duration(987654328), Duration(0), Nil, true))
         tasks should contain key (3)
         tasks should contain value (Task(3, "three", "Tres", Duration(987654327), Duration(0), Nil, false))
         
    }

    it should "store a repository as XML" in {

        val (one, two, three) = (Task(1, "1", "one",   Duration(1.h)),
                                 Task(2, "2", "two",   Duration(2.h)),
                                 Task(3, "3", "three", Duration(3.h)))
        val orepo = new Repository
        orepo.addTask( one )
        orepo.addTask( two )
        orepo.addTask( three )

        orepo.makeCurrent( two )
          
        implicit val testdate = "20110523"
        orepo.allActivities = List(
                                  Activity(1, createdate("11:15:00"), Some(createdate("11:17:00")), "actOne"),
                                  Activity(2, createdate("11:17:00"), Some(createdate("11:34:00")), "actTwo"),
                                  Activity(2, createdate("11:35:00"), Some(createdate("12:00:00")), "actThree"),
                                  Activity(2, createdate("12:00:00"), Some(createdate("12:30:00")), "actFour"),
                                  Activity(3, createdate("12:30:00"), Some(createdate("14:15:00")), "actFive"),
                                  Activity(3, createdate("14:16:00"), Some(createdate("16:00:00")), "actSix")  
                                 )
          
        new Persistence().saveRepo("./Testidometer.xml", orepo)
        
        val nrepo = new Persistence().loadRepo("./Testidometer.xml")
        
        nrepo.allTasks      should equal (orepo.allTasks)
        nrepo.allActivities should equal (orepo.allActivities)

        // nrepo should equal (orepo)    TODO: FAILS!  only the collections are saved and restored, not the state (current task and activity)
    }
}
