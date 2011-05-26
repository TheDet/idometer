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
   
   def newTask(id:Int, title:String, descr:String, estimatedTime:Long=0) { 
       repo.addTask( Task(id, title, descr, Duration(estimatedTime)) ) 
   }

   def listTasks   { repo.allTasks.foreach { println }}
   def listActs    { repo.allActivities.foreach { println }}
   def listCurrent { println ("Task: "+repo.currentTask+"\nActivity: "+repo.currentActivity) }

   def start               { repo.startCurrent() ; repo.currentTask.foreach {t:Task => println ("Task '"+t.title+"' started")}}
   def stop(msg:String="") { repo.stopCurrent(msg); println("Task stopped") }

   def switchTo(sid:Int, msg:String="") {
       repo.stopCurrent(msg)
       repo.allTasks.get(sid).foreach (repo.makeCurrent)  // hint: amap.get(key) returns Option[T]
   } 

   def save(file:String=defaultFile) { new Persistence().saveRepo(file, repo);  println ("File "+file+" stored to disk") }
   def load(file:String=defaultFile) { repo = new Persistence().loadRepo(file); println ("File "+file+" loaded from disk") }
   
   def help = """
   newTask(id:Int, title:String, descr:String, estimatedTime:Long=0)
   listTasks
   listActs 
   start
   stop([msg])
   switchTo(id)  -- stops current task and switches to other task (does not start it!)
   save( [filename] )
   load( [filename] )
   """
}
