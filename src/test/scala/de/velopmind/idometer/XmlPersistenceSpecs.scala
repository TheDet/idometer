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
    "A persistence" should "marshal and unmarshal an activity" in {
         val act = Activity("1", new Date(), Some(new Date()), "actOne")
         val persist = new Persistence()
         val xml = persist.activityToXml(act)
         val actagain = persist.xmlToActivity(xml)
         
         act should equal (actagain)   
    }
    "A persistence" should "marshal and unmarshal a task" in {
        import de.velopmind.idometer.Time._
        val task = Task("one", "one's description",
		                       Duration(2 h),  Duration(0), // will not be restored: Duration(1 h),  
                           Nil, true ) 
//         val act = Activity("1", new Date(), Some(new Date()), "actOne")
         val persist = new Persistence()
         val xml = persist.taskToXml(task)
         val taskagain = persist.xmlToTask(xml)
         
         task should equal (taskagain)   
    }
/*    "A persistence" should "unmarshal a sequence of activities" in {
         def  makeActivityXml(tid:String, start:Long, desc:String) = <activity>
                                  <taskid>{act.taskid}</taskid>
                                  <start>{act.start.getTime}</start>
                                           case None     => <stop />
                                  <desc>{act.descr}</desc>
                               </activity> 

         val xml = <root>
                  {makeActivityXml("one", 987654329, "Uno")}
                  {makeActivityXml("two", 987654328, "Dos")}
                  {makeActivityXml("two", 987654327, "Tres")}
                    </root>
          
         
    }
*/
  "A persistence" should "store a repository as XML" in {
          def mockdate(time:String )(implicit testdate:String) {
            Timestamp.datefactory = () => createdate(time)(testdate) 
          }

         def createdate(time:String )(implicit testdate:String) = {
            import java.text.SimpleDateFormat
            new SimpleDateFormat("yyyymmdd-hh:mm:ss").parse(testdate+"-"+time)
         }

         val (one, two, three) = (Task("1", "one",   Duration(1.h)),
				                             Task("2", "two",   Duration(2.h)),
				                             Task("3", "three", Duration(3.h)))
          val repo = new Repository		                		 
          repo.addTask( one )
          repo.addTask( two )
          repo.addTask( three )

          repo.makeCurrent( two )
          
          repo.allActivities = List(
                                  Activity("1", new Date(), Some(new Date()), "actOne"),
                                  Activity("2", new Date(), Some(new Date()), "actTwo"),
                                  Activity("2", new Date(), Some(new Date()), "actThree"),
                                  Activity("2", new Date(), Some(new Date()), "actFour"),
                                  Activity("3", new Date(), Some(new Date()), "actFive"),
                                  Activity("3", new Date(), Some(new Date()), "actSix")
                                  )
          
          new Persistence().saveRepo("./Testidometer.xml", repo)

    }
}
