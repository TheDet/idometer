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

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import de.velopmind.idometer._

@RunWith( classOf[JUnitRunner])
class ConfigSpec  extends FlatSpec with ShouldMatchers  {

    "A ConfigHandler" should "marshal and unmarshal Configuration" in {
        val conf = Map( ("keyone" -> "value1"), ("keytwo" -> "value2") )
        
        val xml = ConfigHandler.configToXml(conf)
        
        val res = ConfigHandler.xmlToConfig(xml)

        res should equal (conf)   
    }
}