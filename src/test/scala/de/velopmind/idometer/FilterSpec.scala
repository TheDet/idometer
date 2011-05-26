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

import de.velopmind.idometer.Filter._

@RunWith( classOf[JUnitRunner])
class FilterSpec  extends FlatSpec with ShouldMatchers  {
//    import de.velopmind.idometer.Time._
    import TimestampTestUtil._    

   "A Filter" should "filter activities by task id" in {
        implicit val testdate = "20110524"
        def create(descr:String, taskid:Int=1) = 
            Activity(taskid, createdate("10:00:00"), Some(createdate("12:00:00")), descr)
      
        val act1 = create("actOne")
        val act2 = create("actTwo")
        val act3 = create("actThree")
        val actother1 = create("otherOne", 2)
        val actother2 = create("otherTwo", 2)
        val actother3 = create("otherThree", 2)
        
        val acts = List(act1, actother1, act2, act3, actother2, actother3)
        
        val byOne = filterActivitiesByTask(acts, 1 )   
    
        byOne should have size (3)
        byOne should equal ( List(act1, act2, act3) ) 
    }

   "A Filter" should "filter activities by date range" in {
        def createAct(startdate:String, stopdate:String) = 
            Activity(1, toDate(startdate+"-10:00:00"), Some(toDate(stopdate+"-11:00:00")), "Activity for task 1")  
      
        val act1 = createAct("20110501", "20110502")
        val act2 = createAct("20110502", "20110503")
        val act3 = createAct("20110503", "20110504")
        val actother1 = createAct("20110401", "20110402") // before
        val actother2 = createAct("20110429", "20110502") // partly before
        val actother3 = createAct("20110503", "20110505") // partly after
        val actother4 = createAct("20110601", "20110602") // after
        
        val acts = List(actother1, actother2, act1, act2, act3, actother3, actother4)
        
        val byRange = filterActivitiesByDateRange(acts, toDate("20110501-00:00:00"), toDate("20110505-00:00:00") )   
    
        byRange should have size (3)
        byRange should equal ( List(act1, act2, act3) ) 
    }
}
