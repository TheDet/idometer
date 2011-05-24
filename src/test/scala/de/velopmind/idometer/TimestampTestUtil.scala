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
 * Helper object - provides methods for controlled creation of Date objects
 * in the Specs tests.
 */
object TimestampTestUtil {
    import java.text.SimpleDateFormat
    
    /**
     * Sets a Date factory on the Timestamp object.
     * The factory uses the createdate method with the given time parameter and the date implicit parameter
     */
    def mockdate(time:String )(implicit testdate:String) {
        Timestamp.datefactory = () => createdate(time)(testdate) 
    }
    
    /**
     * Creates a Date object by parsing the time parameter and the date implicit parameter
     */
    def createdate(time:String )(implicit testdate:String) = {
        new SimpleDateFormat("yyyymmdd-hh:mm:ss").parse(testdate+"-"+time)
    }
}
