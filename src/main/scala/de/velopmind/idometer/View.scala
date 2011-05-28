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

package de.velopmind.idometer.swing

import scala.swing._
import Swing._

import de.velopmind.idometer._
import de.velopmind.idometer.xml._

import java.util.Locale

object IdometerGui  extends SimpleSwingApplication {
    var repo:Repository = new Repository()
    var currentFile:Option[String] = None
    var currentPanel:Panel = null
    
    var i18n = getTranslation(Locale.ENGLISH)

    val statusBar = new FlowPanel {
        val statustext = new Label
        contents += statustext

        def status(msg:String) { statustext.text = msg}
    }
    
    case class DisplayTask(t:Task) {
         override def toString = ""+t.id+":"+t.title
    }

    val clockPanel = new FlowPanel {
        import FlowPanel._
        import scala.swing.event._
        implicit def taskToDisplay(ts:Iterable[Task]):Seq[DisplayTask] = ts.toList.map( new DisplayTask(_))
        
        val taskSelector = new ComboBox[DisplayTask]( repo.allTasks.values )

        contents += taskSelector
        contents += Button(i18n("b_start")) { start } 
        contents += Button(i18n("b_stop"))  { val msg ="-dummy-" /*TODO: get per Dialog */; stop(msg) }
        
        listenTo(taskSelector.selection)
        reactions += {
           case SelectionChanged(`taskSelector`) => switchTo( taskSelector.selection.item.t.id )
        }
        def updateSelector() { taskSelector.peer.setModel(ComboBox.newConstantModel(repo.allTasks.values)) }
    }

    val mainPanel = new BorderPanel {
        import BorderPanel._

        currentPanel = clockPanel
        add(clockPanel, Position.Center)
        add(statusBar, Position.South)
    }  

    val mainFrame = new MainFrame() {
        preferredSize = (600,400)
        menuBar = new MenuBar {
                     contents += new Menu(i18n("m_file")) {
                       contents += new MenuItem( Action(i18n("i_open"))   { openFile() } )  
                       contents += new MenuItem( Action(i18n("i_save"))   { saveFile() } )  
                       contents += new MenuItem( Action(i18n("i_saveas")) { saveFileAs() } )  
                       contents += new MenuItem( Action(i18n("i_exit")) { System.exit(0)} )  
                     }
                     contents += new Menu(i18n("m_edit")) {
                       contents += new MenuItem( Action("Timed Status")      { timedStatus("Timed Status")} )  
                       contents += new MenuItem( Action("Status")            { statusBar.status("Normal")} )  
                       contents += new MenuItem( Action("Clear Status")      { statusBar.status("")} )  
                     }
                     contents += new Menu(i18n("m_view")) {
                       contents += new MenuItem( Action("Tasks")      { println ("hello")} )  
                       contents += new MenuItem( Action("Activities") { println ("hello")} )  
                       contents += new MenuItem( Action("Stopwatch")  { println ("hello")} )  
                       contents += new MenuItem( Action("DEBUG current")  { println ("File: "+currentFile+"\nTask: "+repo.currentTask+"\nActivity: "+repo.currentActivity)} )  
                       contents += new MenuItem( Action("DEBUG task")  { repo.allTasks.foreach { println }} )  
                       contents += new MenuItem( Action("DEBUG actions")  { repo.allActivities.foreach { println }} )  
                       contents += new MenuItem( Action("DEBUG switch to 3")  { switchTo(3)} )  
                     }
                     contents += new Menu("?") {
                       contents += new MenuItem( Action(i18n("i_about"))      { showInfo } )  
                     }
                  }
//        val x = new Panel() {}
        contents = mainPanel
    }
  
    def top = mainFrame
    
  
    // MENU Actions 

    def showInfo() {
        val message = """
        |  I-do-Meter
        |  Version: 0.1 pre-alpha
        |  Author:  Dirk Detering
        |  Licence: Apache Licence 2.0
        |  Webpage: https://github.com/TheDet/idometer
        """.stripMargin
        Dialog.showMessage(mainPanel, message, "About", Dialog.Message.Info)
    }

    def openFile() {
        val fc = new FileChooser()
        val res = fc.showOpenDialog(mainPanel)
        if (res == FileChooser.Result.Approve) {
            currentFile = Some(fc.selectedFile.getCanonicalPath)
            repo = new Persistence().loadRepo(currentFile.get)
            mainFrame.title = currentFile.get
            clockPanel.updateSelector()
        }
    }

    def saveFile() {
        currentFile.foreach { f=> new Persistence().saveRepo( f , repo) ; timedStatus("File saved")}
    }

    def saveFileAs() {
        val fc = new FileChooser()
        val res = fc.showSaveDialog(mainPanel)
        if (res == FileChooser.Result.Approve) {
            new Persistence().saveRepo(fc.selectedFile.getCanonicalPath, repo) ; timedStatus("File saved")
        }
    }
    
  
    // UTILS 
    
    def timedStatus(msg:String) {
        val timer = Timer.doOnce(4000) { statusBar.status("")}
        statusBar.status(msg)
        timer.start
    }
    
    def getTranslation(loc:Locale):Map[String,String] = {
          import java.util.ResourceBundle
          import scala.collection.JavaConversions._
          val reb = ResourceBundle.getBundle("idomtexts", loc)
          reb.keySet().map {k => (k -> reb.getString(k))}.toMap
    }
    
    def start               { repo.startCurrent() ; repo.currentTask.foreach {t:Task => statusBar.status("Task '"+t.title+"' started at: "+Timestamp())}}
    def stop(msg:String="") { val curname = repo.currentTask.map(_.title).getOrElse("-none-"); repo.stopCurrent(msg); statusBar.status("Task "+curname+" stopped at: "+Timestamp()) }

    def switchTo(sid:Int, msg:String="") {
       stop(msg)
       repo.allTasks.get(sid).foreach (repo.makeCurrent)  // hint: amap.get(key) returns Option[T]
       statusBar.status("Current task: "+repo.currentTask.map(_.title).getOrElse("-none-")+" (stopped)")
   } 

 
}






object Timer {  // TODO: This construct should be based on scala.swing.Swing.ActionListener, scala.swing.event.ActionEvent
  import javax.swing.Timer
  import java.awt.event.{ActionListener, ActionEvent}

  class TimerListener(f: =>Unit) extends ActionListener {
      override def actionPerformed(evt:ActionEvent) { f }
  }

  def doOnce(delay:Int)(f: =>Unit) = {
      val timer = new javax.swing.Timer(delay, new TimerListener(f))
      timer.setRepeats(false)
      timer
  } 
  
  def doAlways(delay:Int)(f: =>Unit) = new javax.swing.Timer(delay, new TimerListener(f))
}

