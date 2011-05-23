import sbt._
import netbeans.plugin._

class IdometerProject(info: ProjectInfo) extends DefaultProject(info) with SbtNetbeansPlugin
{
    val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.4.1"
    val scalaSwing = "org.scala-lang" % "scala-swing" % "2.9.0"
}
