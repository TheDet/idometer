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

package de.velopmind.idometer

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import java.util.Date

@RunWith( classOf[JUnitRunner])
class DurationSpec  extends FlatSpec with ShouldMatchers  {

    "A Duration" should "return the same miliseconds span used to construct it" in {
        val miliseconds = 12345678L
        Duration(miliseconds).toLong should equal (miliseconds)
    }

    it should "represent a miliseconds span as hours (round down)" in {
        val duration = Duration( (2 * 60 + 10) * 60 * 1000 )  // 2h 10m
        duration.asHours should equal (2)
    }
    
    it should "represent a miliseconds span as time in hh:mm format" in {
        val shortDuration = Duration( (2 * 60 + 10) * 60 * 1000 )  // 2h 10m
        shortDuration.asTime should equal ("2:10")
        val longDuration = Duration( (26 * 60 + 10) * 60 * 1000 )  // 26h 10m
        longDuration.asTime should equal ("26:10")
    }
    
    it should "support addition" in {
        val first  = Duration(300)
        val second = Duration(900)
        (first + second) should equal (Duration(1200))
    }

}



@RunWith( classOf[JUnitRunner])
class TimeSpec  extends FlatSpec with ShouldMatchers  {
    import de.velopmind.idometer.Time._

    "Time" should "represent long number as hours" in {
        val time = 5.h
        time should equal ( 5 * 60 * 60 * 1000)
    }
}



@RunWith( classOf[JUnitRunner])
class TimestampSpec  extends FlatSpec with ShouldMatchers  {
    import de.velopmind.idometer.Timestamp

    "Timestamp" should "return current date" in {
        val time = Timestamp()
        val curr = new Date()
        time should equal(curr)   // ATT: Only true if the above lines run in the same milisecond 
    }

    it should "return current date as long" in {
        val time = Timestamp.time
    //time should equal ( 5 * 60 * 60 * 1000)
    }

    it should "use set factory" in {
        import java.text.SimpleDateFormat
        val longvalue = new SimpleDateFormat("dd.mm.yyyy").parse("03.04.2011").getTime
        val defaultfactory = Timestamp.defaultfactory
        Timestamp.datefactory should be (defaultfactory)
        Timestamp.datefactory = () => new Date(longvalue)
      
        val fixtime = Timestamp()
        val fixtimed = Timestamp.date
        val fixtimelong = Timestamp.time
      
        fixtime should equal (fixtimed)
        fixtime.getTime should equal (fixtimelong)
        fixtimelong should equal (longvalue)
        //time should equal ( 5 * 60 * 60 * 1000)
    }
}


@RunWith( classOf[JUnitRunner])
class ActivitySpec  extends FlatSpec with ShouldMatchers  {

    "An Activity " should "start" in {
        val activity = Activity(1, new Date(1000000L))
        activity.start should equal (new Date(1000000L))
        activity.stop  should be (None)
        activity.descr should be ("")
    }

    it should "start with default time 'now'" in {
        val activity = Activity(1)
        activity.start should not be null
        activity.start.getClass() should be (classOf[java.util.Date])
        activity.stop  should be (None)
        activity.descr should be ("")
    }

    it should "stop" in {
        val startDate = new Date()
        val activity = Activity(1, startDate).finish
        activity.start should equal (startDate)
        activity.stop  should not be (None)
        activity.descr should be ("")
    }

    it should "take comment" in {
        val startDate = new Date()
        var activity = Activity(1, startDate).finish

        activity.start should equal (startDate)

        val stoptime = activity.stop
        stoptime  should not be (None)

        activity = activity.comment("Some Test")

        // Text shall be set, the rest must not be changed
        activity.start should equal (startDate)
        activity.stop  should equal (stoptime)
        activity.descr should be    ("Some Test")
    }

    it should "return duration" in {
        val startDate = new Date()
        val activity = Activity(1, startDate).finish

        activity.start should equal (startDate)

        val stoptime = activity.stop
        stoptime  should not be (None)

        val timespan = stoptime.get.getTime - startDate.getTime
        val duration = Duration(timespan)

        activity.duration should equal (duration)
    }
}

@RunWith( classOf[JUnitRunner])
class TaskSpec  extends FlatSpec with ShouldMatchers  {
    import de.velopmind.idometer.Time._

    "A Task" should "be initialised with default values" in {
        val task = Task(1, "one", "first task", Duration(5.h))
        task.id            should be (1)
        task.title         should be ("one")
        task.descr         should be ("first task")
        task.estimatedTime should equal (Duration(5.h))
        task.consumedTime  should equal (Duration(0))
        task.finished      should be (false)
    }

    it should "consume activity" in {
        val task     = Task(1, "1", "first", Duration(5.h))
        val activity = Activity(1, start = new Date(1000), stop = Some(new Date(130 * 1000)))
        val consumed = task.consume(activity) 
        consumed.consumedTime.asMinutes should equal (2) // 130 seconds rounded down
    }
}

@RunWith( classOf[JUnitRunner])
class RepositorySpec   extends FlatSpec with ShouldMatchers  {
    import de.velopmind.idometer.Time._

    "The Repository" should "accept Tasks" in {
        val (one, two, three) = (Task(1, "1", "one",   Duration(1.h)),
                                 Task(2, "2", "two",   Duration(2.h)),
                                 Task(3, "3", "three", Duration(3.h)))
        val repo = new Repository                                 
        repo.addTask( one )
        repo.addTask( two )
        repo.addTask( three )
        
        repo.allTasks.size should equal (3) 
    }

    it should "make Task current" in {
        val (one, two, three) = (Task(1, "1", "one",   Duration(1.h)),
                                 Task(2, "2", "two",   Duration(2.h)),
                                 Task(3, "3", "three", Duration(3.h)))
        val repo = new Repository                                 
        repo.addTask( one )
        repo.addTask( two )
        repo.addTask( three )
        
        repo.makeCurrent( two )
        repo.currentTask.get should equal ( two ) 
    
        repo.makeCurrent( three )
        repo.currentTask.get should equal ( three ) 
    }

    it should "start and stop current Task" in {
        import TimestampTestUtil._    
    
        def checkactivity(a:Activity, taskid:Int, desc:String, start:Date=null, stop:Option[Date]=None) {
            a.taskid should be (taskid)
            a.descr  should be (desc)
            if (start != null) a.start  should be (start)
            if (stop  != None) a.stop   should be (stop)
        }
    
        implicit val testdate = "20110403"
        val one = Task(1, "1", "one", Duration(1.h))
        val repo = new Repository                                 
        repo.addTask( one )
        repo.makeCurrent( one )
        mockdate("10:32:05")
        repo.startCurrent()
        repo.allActivities.size should equal (0)
        val act = repo.currentActivity
        act should not equal (None)
        checkactivity(act.get, 1, "", createdate("10:32:05"), None) //TODO : further verify act !
        mockdate("10:33:35")
        repo.stopCurrent("stopit")
    
        repo.allActivities.size should equal (1)
        checkactivity(repo.allActivities(0), 1, "stopit", createdate("10:32:05") , Some(createdate("10:33:35")))    

        mockdate("10:42:42")
        repo.startCurrent()
        mockdate("10:46:42")
        repo.stopCurrent("stopagain")

        repo.allActivities.size should equal (2)
        checkactivity(repo.allActivities(1), 1, "stopit", createdate("10:32:05") , Some(createdate("10:33:35")))    
        checkactivity(repo.allActivities(0), 1, "stopagain", createdate("10:42:42"), Some(createdate("10:46:42")))    

        Timestamp.datefactory = Timestamp.defaultfactory
    }
}
