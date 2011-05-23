package de.velopmind.idometer

import scala.swing._
import Swing._

object TimeControlGui  extends SimpleSwingApplication {
  val mainFrame = new Frame() {
        menuBar = new MenuBar {
                     contents += new Menu("File") {
                       contents += new MenuItem( Action("hello") { println ("hello")} )  
                       contents += new MenuItem( Action("hugo") { println ("hello")} )  
                     }
                  }
        val x = new Panel() {}
        contents = new BorderPanel {
            import BorderPanel._
        }
  }
  
  def top = mainFrame
}
