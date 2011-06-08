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

import IdometerGui._
import scala.swing._
import event._
import Dialog._

abstract class EditDialog[M](owner:Window, _model:M) extends Dialog(owner) {
    
    protected var model:M = _model
    protected var _result:Result.Value = Result.Cancel //default
    
    val confirm   = new Button(i18n("b_confirm"))
    val cancel    = new Button(i18n("b_cancel"))
    val buttonBar = new FlowPanel {  contents += confirm
                                     contents += cancel } 
    val framePanel = new BorderPanel {
        import BorderPanel._

        add(buttonBar, Position.South)

        def addContent(c:Component) { add(c, Position.Center) } //method necessary, as add is protected. But better encapsulates the Center design anyway
    }
    
    super.contents = framePanel

    listenTo(confirm, cancel)
    
    reactions += {
      case ButtonClicked(`confirm`) => _result = Result.Ok     ; dispose()
      case ButtonClicked(`cancel`)  => _result = Result.Cancel ; model = _model; dispose()
    }

    override def contents_=(c:Component) {  framePanel.addContent(c)  } 
    
    def result = _result
    def get:M  = model

    def apply():M = {  pack ; open ; get  }   // TODO: In some cases this better gives 
                                              // an Option[M], where None = Cancel | OR: Either??
}
