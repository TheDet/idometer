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

import scala.swing._
import Swing._

import de.velopmind.idometer.xml._

object IdometerGui  extends SimpleSwingApplication {
  var repo:Repository = new Repository()
  var currentFile:Option[String] = None

  val mainPanel = new BorderPanel {
            import BorderPanel._
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
                       contents += new MenuItem( Action("Open")      { println ("hello")} )  
                       contents += new MenuItem( Action("Save")      { println ("hello")} )  
                       contents += new MenuItem( Action("SaveAs...") { println ("hello")} )  
                     }
                     contents += new Menu("View") {
                       contents += new MenuItem( Action("Tasks")      { println ("hello")} )  
                       contents += new MenuItem( Action("Activities")      { println ("hello")} )  
                       contents += new MenuItem( Action("Stopwatch") { println ("hello")} )  
                     }
                  }
//        val x = new Panel() {}
        contents = mainPanel
  }
  
  def top = mainFrame
  
    
  def openFile() {
    val fc = new FileChooser()
    val res = fc.showOpenDialog(mainPanel)
    if (res == FileChooser.Result.Approve) {
        currentFile = Some(fc.selectedFile.getCanonicalPath)
        repo = new Persistence().loadRepo(currentFile.get)
        mainFrame.title = currentFile.get
    }
  }
  
  def saveFile() {
    currentFile.foreach { f=> new Persistence().saveRepo( f , repo) }
  }

  def saveFileAs() {
    val fc = new FileChooser()
    val res = fc.showSaveDialog(mainPanel)
    if (res == FileChooser.Result.Approve) {
        new Persistence().saveRepo(fc.selectedFile.getCanonicalPath, repo)
    }
  }

}
