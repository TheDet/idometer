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

object IdometerGui  extends SimpleSwingApplication {
    var repo:Repository = new Repository()
    var currentFile:Option[String] = None

    val statusBar = new FlowPanel {
        val statustext = new Label
        contents += statustext

        def status(msg:String) { statustext.text = msg}
    }
    
    class DisplayTask(t:Task) {
         override def toString = ""+t.id+":"+t.title
    }

    val clockPanel = new FlowPanel {
        import FlowPanel._
        implicit def taskToDisplay(ts:Iterable[Task]):Seq[DisplayTask] = ts.toList.map( new DisplayTask(_))
        
        val taskSelector = new ComboBox[DisplayTask]( repo.allTasks.values )

        contents += taskSelector
        contents += Button("Start") {}
        contents += Button("Stop")  {}
        
        def updateSelector() { taskSelector.peer.setModel(ComboBox.newConstantModel(repo.allTasks.values))}
    }

    val mainPanel = new BorderPanel {
        import BorderPanel._

        add(clockPanel, Position.Center)
        add(statusBar, Position.South)
    }  

    val mainFrame = new MainFrame() {
        preferredSize = (600,400)
        menuBar = new MenuBar {
                     contents += new Menu("File") {
                       contents += new MenuItem( Action("Open")      { openFile() } )  
                       contents += new MenuItem( Action("Save")      { saveFile() } )  
                       contents += new MenuItem( Action("SaveAs...") { saveFileAs() } )  
                       contents += new MenuItem( Action("Exit") { System.exit(0)} )  
                     }
                     contents += new Menu("Edit") {
                       contents += new MenuItem( Action("Timed Status")      { timedStatus("Timed Status")} )  
                       contents += new MenuItem( Action("Status")            { statusBar.status("Normal")} )  
                       contents += new MenuItem( Action("Clear Status")      { statusBar.status("")} )  
                     }
                     contents += new Menu("View") {
                       contents += new MenuItem( Action("Tasks")      { println ("hello")} )  
                       contents += new MenuItem( Action("Activities") { println ("hello")} )  
                       contents += new MenuItem( Action("Stopwatch")  { println ("hello")} )  
                     }
                     contents += new Menu("?") {
                       contents += new MenuItem( Action("About")      { showInfo } )  
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

