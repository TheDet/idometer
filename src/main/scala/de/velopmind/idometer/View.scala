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

object IdometerGui  extends SimpleSwingApplication {
  var repo:Repository = new Repository()
  
  val mainFrame = new Frame() {
        preferredSize = (500,300)
        menuBar = new MenuBar {
                     contents += new Menu("File") {
                       contents += new MenuItem( Action("Open") { println ("hello")} )  
                       contents += new MenuItem( Action("Save") { println ("hello")} )  
                       contents += new MenuItem( Action("SaveAs...") { println ("hello")} )  
                     }
                  }
        val x = new Panel() {}
        contents = new BorderPanel {
            import BorderPanel._
        }
  }
  
  def top = mainFrame
}
