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

/**
 * This is no fully fledged Command line tool, it is only a proof of concept UI
 * to test the provided actions of the user interface
 * 
 * So you are expected to run it in a Scala REPL with I-do-Meter on the classpath
 * In the REPL you should import de.velopmind.idometer.Time._ to get a Time API.
 * Then you should import de.velopmind.idometer.CmdLineUI._ so you have the below 
 * commands to simulate user interaction.
 */
object CmdLineUI {
   //import de.velopmind.idometer.Time._
   import de.velopmind.idometer.xml.Persistence

   val defaultFile = "./TestConsoleIdometer.xml"
  
   var repo = new Repository()
   
   def newTask(id:String, descr:String, estimatedTime:Long=0) { repo.addTask( Task(id, descr, Duration(estimatedTime)) ) }
   def listTasks { repo.allTasks.foreach { println }}
   def listActs { repo.allActivities.foreach { println }}
   def listCurrent { println ("Task: "+repo.currentTask+"\nActivity: "+repo.currentActivity) }
   def selectTask {}
   def start { repo.startCurrent() }
   def stop { repo.stopCurrent()}
   def switchTo(sid:String) { repo.allTasks.get(sid).foreach (repo.makeCurrent) } // hint: amap.get(key) returns Option[T]
   //def switchTo(sid:String) { repo.makeCurrent( repo.allTasks.get(sid) )  }
   def save { new Persistence().saveRepo(defaultFile, repo) }
   def load { repo = new Persistence().loadRepo(defaultFile) }
   
   def help = """
   newTask(id:String, descr:String, estimatedTime:Long=0)
   listTasks
   selectTask
   start
   stop
   switchTo(id)
   save
   load
   """
}
