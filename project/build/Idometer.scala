import sbt._
import netbeans.plugin._

class IdometerProject(info: ProjectInfo) extends DefaultProject(info) with SbtNetbeansPlugin
{
    val scalatest  = "org.scalatest" % "scalatest_2.9.0" % "1.4.1"
    val scalaSwing = "org.scala-lang" % "scala-swing" % "2.9.0"
    val junit      = "junit" % "junit" % "4.8.2"
    val grizzled   = "org.clapper" %% "grizzled-slf4j" % "0.5"    
    val slf4j      = "org.slf4j" % "slf4j-log4j12" % "1.6.1"    

    override def mainClass = Some("de.velopmind.idometer.swing.IdometerGui")
}
