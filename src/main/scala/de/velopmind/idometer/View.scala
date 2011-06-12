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
import event.Key

import de.velopmind.idometer._
import de.velopmind.idometer.xml._

import java.util.Locale

import grizzled.slf4j.Logging




object IdometerGui  extends SimpleSwingApplication {
   
    val FILE_EXTENSION = "ido"

    var i18n = getTranslation(Locale.GERMAN)

    val mainController = new MainController
    
    def top = mainController.mainFrame
    
  
    // UTILS 
    
    def timedStatus(msg:String) {
        mainController.status(msg)
        Timer.doOnce(4000) { mainController.status("")}.start
    }
    
    def getTranslation(loc:Locale):Map[String,String] = {
          import java.util.ResourceBundle
          import scala.collection.JavaConversions._
          val reb = ResourceBundle.getBundle("idomtexts", loc)
          reb.keySet().map {k => (k -> reb.getString(k))}.toMap
    }
    
    import scala.swing.event.Event
    class Started extends Event
    class Stopped extends Event
}




class MainController extends Publisher with Logging {
    import IdometerGui._
    import scala.swing.event._
    import java.io.File
    
    var repo:Repository = new Repository()
    var currentFile:Option[String] = None

    var config = ConfigHandler.loadConfig

    val watch = new WatchController(this)
    val mainFrame = new IdometerFrame(this)
  

    def start { 
       repo.startCurrent()
       repo.currentTask.foreach {t:Task => status(i18n("msg_taskstarted").format(t.title, Timestamp()))}
       publish(new Started)
     }

    def stop(msg:String="") { 
        val curname = repo.currentTask.map(_.title).getOrElse("-none-") 
        repo.stopCurrent(msg) 
        status(i18n("msg_taskstopped").format(curname, Timestamp())) 
        publish(new Stopped)
      }

    def switchTo(sid:Int, msg:String="") {
        stop(msg)
        repo.allTasks.get(sid).foreach (repo.makeCurrent)  // hint: amap.get(key) returns Option[T]
   } 

    def openFile() {
        val fc = fileChooser
        val res = fc.showOpenDialog(mainFrame.mainPanel)
        if (res == FileChooser.Result.Approve) {
            currentFile     = Some(fc.selectedFile.getCanonicalPath)
            repo            = new Persistence().loadRepo(currentFile.get)
            mainFrame.title = currentFile.get
            watch.updateSelector()
        }
    }

    def saveFile() {
        currentFile match {
          case Some(f) => new Persistence().saveRepo( f , repo) ; timedStatus(i18n("msg_filesaved").format(f))
          case None => saveFileAs()
        }

    }

    def saveFileAs() {
        val fc = fileChooser
        val res = fc.showSaveDialog(mainFrame.mainPanel)
        if (res == FileChooser.Result.Approve) {
            val fs = fileWithExtension(fc.selectedFile)
            new Persistence().saveRepo(fs.getCanonicalPath, repo)
            timedStatus(i18n("msg_filesaved").format(fs.getName()))
        }
    }

    def createTask() {
        val keys  = repo.allTasks.keys
        val ted = new TaskEditDialog(mainFrame, Task( if (keys.size > 0) keys.max+1 else 1, "", "", Duration(0)))
        val task = ted()
        if ( ted.result == Dialog.Result.Ok) {  // PERHAPS: Option[M] better result??
           repo.addTask(task)
           watch.updateSelector   // TODO: leads to misbehaviour - see issue #3
        }
    }

    def editConfig() {
        debug (config)
        val od = new OptionDialog(mainFrame, config)
        config = od()
        debug (config)
    }

    def saveConfig() { ConfigHandler.saveConfig(config) }


  
    def status(msg:String) { mainFrame.statusBar.status(msg)}

    def showInfo() {
        val message = """
        |  I-do-Meter
        |  Version: 0.0.2 pre-alpha
        |  Author:  Dirk Detering
        |  Licence: Apache Licence 2.0
        |  Webpage: https://github.com/TheDet/idometer
        """.stripMargin
        Dialog.showMessage(mainFrame.mainPanel, message, "About", Dialog.Message.Info)
    }
    
  
    // Helper methods
    def fileChooser = {
        import javax.swing.filechooser.FileNameExtensionFilter
        val fc = new FileChooser()
        fc.fileFilter = new FileNameExtensionFilter("I-do-Meter Files", FILE_EXTENSION)
        fc
    }
 
    def fileWithExtension(f:File) = {
        val ext = "."+FILE_EXTENSION
        if ( ! f.getName().toLowerCase().endsWith(ext) )  
           new File(f.getCanonicalPath + ext)
        else
          f
    }
}



class IdometerFrame(ctrl:MainController) extends MainFrame with Logging {
        import IdometerGui._
      
        title = i18n("t_unknown")

        val statusBar = new FlowPanel {
            val statustext = new Label
            contents += statustext

            def status(msg:String) { statustext.text = msg}
        }

        val mainPanel = new BorderPanel {
            import BorderPanel._

            val contentPanel = new FlowPanel() { def removeAll { peer.removeAll }}
            add(contentPanel, Position.Center)
            add(statusBar, Position.South)

//            def switchPanel(p:Panel) { contentPanel.contents.clear() ; contentPanel.contents += p}   TODO
            contentPanel.contents += ctrl.watch.view  // how to make this exchangeable?
        }  


  
        preferredSize = (600,400)
        menuBar = new MenuBar {
                     contents += new Menu(i18n("m_file")) {
                       mnemonic = Key.withName(text.substring(0,1))
                       contents += new MenuItem( Action(i18n("i_open"))   { ctrl.openFile() } )  
                       contents += new MenuItem( Action(i18n("i_save"))   { ctrl.saveFile() } )  
                       contents += new MenuItem( Action(i18n("i_saveas")) { ctrl.saveFileAs() } )  
                       contents += new MenuItem( Action(i18n("i_exit"))   { System.exit(0)} )  
                     }
                     contents += new Menu(i18n("m_edit")) {
                       mnemonic = Key.withName(text.substring(0,1))
                       contents += new MenuItem( Action("Timed Status")      { timedStatus("Timed Status")} )  
                       contents += new MenuItem( Action("Status")            { ctrl.status("Normal")} )  
                       contents += new MenuItem( Action("Clear Status")      { ctrl.status("")} )  
                       contents += new MenuItem( Action(i18n("i_createtask"))   { ctrl.createTask()} )  
                       contents += new MenuItem( Action(i18n("i_options"))      { ctrl.editConfig()} )  
                     }
                     contents += new Menu(i18n("m_view")) {
                       mnemonic = Key.withName(text.substring(0,1))
                       contents += new MenuItem( Action("Tasks")      { info ("hello")} )  
                       contents += new MenuItem( Action("Activities") { info ("hello")} )  
                       contents += new MenuItem( Action("Watch")      { info ("hello")} )  
                     }
                     contents += new Menu("Debug") {
                       contents += new MenuItem( Action("DEBUG current")  { info ("File: "+ctrl.currentFile+"\nTask: "+ctrl.repo.currentTask+"\nActivity: "+ctrl.repo.currentActivity)} )  
                       contents += new MenuItem( Action("DEBUG task")     { ctrl.repo.allTasks.foreach { info (_)}} )  
                       contents += new MenuItem( Action("DEBUG actions")  { ctrl.repo.allActivities.foreach { info(_)}} )  
                     }
                     contents += new Menu("?") {
                       contents += new MenuItem( Action(i18n("i_about"))      { ctrl.showInfo } )  
                     }
                  }

      contents = mainPanel
}


case class DisplayTask(t:Task) {
     override def toString = ""+t.id+":"+t.title
}

object ConfigHandler {     // TODO: Needs redesign: config has an internally known set of keys. That should be expressed in the xml
    import java.io.File
    import scala.xml._
    
    val home = System.getProperty("user.home")
    val conffile = ".idometer.conf"
    val file = new File(home+File.separator+conffile)
  
    def loadConfig() = {
        if (file.canRead)  xmlToConfig( XML.loadFile(file) ) 
        else               Map[String,String]()                  // EMPTY?? Or default values for the known keys??
    }
    
    def saveConfig(config:Map[String,String]) {
        if (file.canWrite)  XML.save(file.getCanonicalPath, configToXml(config), xmlDecl=true )
    }
   
    def xmlToConfig(n:Node):Map[String,String] = 
        (n \ "conf").foldLeft( Map[String,String]() ) { (m:Map[String,String], n:Node)  => m + ((n \ "name").text -> (n \ "value").text) }
 
  
    def configToXml(config:Map[String,String]) = 
        <configuration>
        {for (c <- config) yield <conf><name>{c._1}</name><value>{c._2}</value></conf>}
        </configuration>
}





class WatchController(main:MainController) extends Reactor {
      import IdometerGui._
      import scala.swing.event._
      import javax.swing.ComboBoxModel
      
      implicit def taskToDisplay(ts:Iterable[Task]):Seq[DisplayTask] = ts.toList.map( new DisplayTask(_))

      val view = new WatchPanel
      val startButton  = view.startButton
      val stopButton   = view.stopButton
      val taskSelector = view.taskSelector
      
      stoppedState; startButton.enabled = false

      listenTo(main)
      reactions += {
        case e:Started => startedState
        case e:Stopped => stoppedState
      }
  
      view.listenTo(startButton, stopButton, taskSelector.selection)
      
      view.reactions += {
             case ButtonClicked(`startButton`)     => main.start//; startedState
             case ButtonClicked(`stopButton`)      => main.stop( getMessage() ) //; stoppedState
             case SelectionChanged(`taskSelector`) => main.switchTo( taskSelector.selection.item.t.id, getMessage() )//; stoppedState 
      }
     
      def getMessage() = new MessageDialog(main.mainFrame,"")()
      def updateSelector() { taskSelector.setModel(ComboBox.newConstantModel(main.repo.allTasks.values))
                             taskSelector.selection.index = 0  // TODO: Leads to misbehaviour - see issue #3
      }
      
      def startedState { startButton.enabled = false; stopButton.enabled = true }
      def stoppedState { startButton.enabled = true;  stopButton.enabled = false }
}


class WatchPanel extends BorderPanel {
      import javax.swing.ComboBoxModel
      import IdometerGui._
      import BorderPanel._

      class Selector extends ComboBox[DisplayTask](List[DisplayTask]()) { 
        def setModel(m:ComboBoxModel) { peer.setModel(m)} 
      }
      
      val taskSelector = new Selector { preferredSize = (220,25)}
      val startButton  = new Button(i18n("b_start"))
      val stopButton   = new Button(i18n("b_stop")) 

      val upperPanel = new FlowPanel{
          import FlowPanel._

          contents += new Label(i18n("l_task"))
          contents += taskSelector
          contents += startButton 
          contents += stopButton
      }
      
      add(upperPanel, Position.North)
}




class TaskEditDialog(owner: Window, task:Task) extends EditDialog[Task](owner, task) {
    import IdometerGui._
    import scala.swing._
    import scala.swing.event._

    modal = true
    preferredSize = (500,300)
    location = (20,20)
    setLocationRelativeTo(owner)

    title = i18n("t_edittask")
    
  
    class ValueField(val key:String, value:String, col:Int=0) extends TextField(value, col) 
    
    val grid = new GridBagPanel { import GridBagPanel._
                   val c = new Constraints
                   
                   val ftitle = new TextField(20)
                   val fdescr = new TextField(20)
                   val festim = new TextField(20)
                   c.gridx = 0 ; c.gridy = 0            
                   layout(new Label(i18n("l_task"))) = c  // maps to Task.title         
                   c.gridx = 1 ; c.gridy = 0            
                   layout(ftitle) = c           
                   c.gridx = 0 ; c.gridy = 1            
                   layout(new Label(i18n("l_descr"))) = c           
                   c.gridx = 1 ; c.gridy = 1            
                   layout(fdescr) = c           
                   c.gridx = 0 ; c.gridy = 2            
                   layout(new Label(i18n("l_estim"))) = c           
                   c.gridx = 1 ; c.gridy = 2            
                   layout(festim) = c           
                   
                   ftitle.text = model.title
                   fdescr.text = model.descr
                   festim.text = model.estimatedTime.asHours.toString

                   listenTo(ftitle, fdescr, festim)
                   reactions += {
                     case EditDone(`ftitle`) => model = model.copy( title = ftitle.text)
                     case EditDone(`fdescr`) => model = model.copy( descr = fdescr.text)
                     case EditDone(`festim`) => model = model.copy( estimatedTime = Duration(festim.text.toInt * 3600000))
                   }
               }
               
     contents = grid
}


class MessageDialog(owner: Window, msg:String) extends EditDialog[String](owner, msg) {
    import IdometerGui._
    import scala.swing._
    import scala.swing.event._

    modal = true
    preferredSize = (300,300)
    location = (10,10)
    setLocationRelativeTo(owner)

    title = i18n("t_message")
    
    contents = new FlowPanel { import GridBagPanel._
                   val editor = new TextArea(msg, 12,20)
                   //val editor = new TextField(msg, 20)

                   contents += new ScrollPane(editor)
                   listenTo(editor)
                                              
                   reactions += {
                     case FocusLost(editor:TextArea,_,_) => model = editor.text
                   }
               }
}




class OptionDialog(owner: Window, config:Map[String,String]) extends EditDialog[Map[String,String]](owner, config) {
    import IdometerGui._
    import scala.swing._
    import scala.swing.event._

    modal = true
    preferredSize = (500,300)
    location = (10,10)
    setLocationRelativeTo(owner)

    title = i18n("t_options")
    
  
    class ValueField(val key:String, value:String, col:Int=0) extends TextField(value, col) 
    
    val grid = new GridBagPanel { import GridBagPanel._
                   val c = new Constraints 
                   model.foldLeft(0) { (r,e) =>  c.gridx = 0 ; c.gridy = r
                         layout(new Label(e._1+": ")) = c
                         val inp = new ValueField(e._1, e._2, 20)
                         c.gridx = 1
                         layout(inp) = c
                         listenTo(inp)
                         r + 1
                   }
                                              
                   reactions += {
                     case EditDone(field:ValueField) => model += (field.key -> field.text)
                   }
               }
               
     contents = new ScrollPane( grid )
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

