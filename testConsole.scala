/*
 * This is a test script, meant to be loaded into a REPL with idometer on classpath.
 * 
 * It imports the necessary things and provides methods to get a test environment
 * up very quickly.
 */

import de.velopmind.idometer.Time._
import de.velopmind.idometer.Filter._
import de.velopmind.idometer.CmdLineUI._

def fillTasks {
   newTask("one", "First Task")
   newTask("two", "Second Task", 5 h )
   newTask("three", "Third Task", 8 h )
}
