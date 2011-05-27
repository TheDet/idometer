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
   var currentFile = defaultFile
   var repo = new Repository()
   
   def newTask(title:String, descr:String, estimatedTime:Long=0) { 
     repo.addTask( Task(repo.allTasks.keys.max + 1, title, descr, Duration(estimatedTime)) ) 
   }

   def tasks   { repo.allTasks.foreach { println }}
   def acts    { repo.allActivities.foreach { println }}
   def current { println ("File: "+currentFile+"\nTask: "+repo.currentTask+"\nActivity: "+repo.currentActivity) }

   def start               { repo.startCurrent() ; repo.currentTask.foreach {t:Task => println ("Task '"+t.title+"' started at: "+Timestamp())}}
   def stop(msg:String="") { val curname = repo.currentTask.map(_.title).getOrElse("-none-"); repo.stopCurrent(msg); println("Task "+curname+" stopped at: "+Timestamp()) }

   def switchTo(sid:Int, msg:String="") {
       stop(msg)
       repo.allTasks.get(sid).foreach (repo.makeCurrent)  // hint: amap.get(key) returns Option[T]
       println ("Current task: "+repo.currentTask.map(_.title).getOrElse("-none-")+" (stopped)")
   } 

   def save                { new Persistence().saveRepo(currentFile, repo);  println ("File "+currentFile+" stored to disk") }
   def saveAs(file:String) { new Persistence().saveRepo(file, repo);  println ("File "+file+" stored to disk") }
   def load(file:String=defaultFile) { repo = new Persistence().loadRepo(file); currentFile=file; println ("File "+file+" loaded from disk") }
   
   // TODO: Only experimental here - has to be refactored into other sources.  1.) The consolidation of data  2.) The report layouting
   def report {
     repo.allTasks.foreach { tentry =>
       val x = tentry._2.consume(repo.allActivities.filter( _.taskid == tentry._1 )) 
       println (x)
       println (x.activities)
       println ("==========================================================================================")
     }
   }
   
   def help = """
   newTask(title:String, descr:String, estimatedTime:Long=0)
   tasks
   acts 
   current
   start
   stop([msg])
   switchTo(id [,msg])  -- stops current task and switches to other task (does not start it!)
   save                 -- saves current file to disk
   saveAs( [filename] ) -- saves file as filename (does not switch current file name!)
   load( [filename] )
   """
}
