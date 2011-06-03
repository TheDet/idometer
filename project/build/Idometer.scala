import sbt._
import netbeans.plugin.SbtNetbeansPlugin
import org.clapper.sbtplugins.IzPackPlugin
import org.clapper.sbtplugins.izpack._

class IdometerProject(info: ProjectInfo) extends DefaultProject(info) with SbtNetbeansPlugin with IzPackPlugin
{
    val scalatest  = "org.scalatest" % "scalatest_2.9.0" % "1.4.1"
    val scalaSwing = "org.scala-lang" % "scala-swing" % "2.9.0"
    val junit      = "junit" % "junit" % "4.8.2"
    val grizzled   = "org.clapper" %% "grizzled-slf4j" % "0.5"    
    val slf4j      = "org.slf4j" % "slf4j-log4j12" % "1.6.1"    

    override def mainClass = Some("de.velopmind.idometer.swing.IdometerGui")
    
  
    // IzPack - config
    lazy val installConfig = new IzPackConfig("target" / "install", log) {
      val TargetDocDir = "target" / "doc"
      new Info {
          appName = projectName.value.toString
          appSubPath = appName  
          appVersion = projectVersion.value.toString
          author("Dirk Detering", "mailtodet@googlemail.com")
          createUninstaller = true                       // Default anyway
          javaVersion = "1.6"
          pack200 = false                                // Default anyway
          requiresJDK = false                            // Default anyway
          runPrivileged = false                          // Default anyway
          summaryLogFilePath = "$INSTALL_PATH/log.html"
          url = "https://github.com/TheDet/idometer/"
          writeInstallationInfo = true                   // Default anyway
      }
      
      languages = List("eng", "deu")
 
      new Packaging {    // Should be default ...
          packager = Packager.SingleVolume    
      }
      
      new GuiPrefs {
          height = 400
          width  = 700
      }

      new Resources{
          new InstallDirectory
          {
              """C:\Program Files\Idometer""" on Windows
              "/Applications/Idometer"        on MacOSX
              "/usr/local/idometer"           on Unix
          }
      }
      
      new Panels {
          new Panel("TargetPanel")
          new Panel("PacksPanel")
          new Panel("InstallPanel")
          new Panel("FinishPanel")
      }
      
      new Packs {
        new Pack("Core") {
            required = true
            // preselected = true  is true because of required
            description = "Basic Application"
            
//            new File(jarPath, "$INSTALL_PATH/lib")
            new File("bin", "$INSTALL_PATH")
            new Executable("$INSTALL_PATH/bin/idometer.sh") {
                onlyFor(Unix, MacOSX)
                //failure=FailureType.Warn   is a val, cannot be set
            }        
            new Executable("$INSTALL_PATH/bin/idometer.bat") {
                onlyFor(Windows)
                //failure=FailureType.Warn     is a val, cannot be set
            }        
        
        
        
            new SingleFile(jarPath, "$INSTALL_PATH/lib/idometer-"+projectVersion.value.toString+".jar")

        
        
            val projectBootDir = "project" / "boot" / ("scala-" + buildScalaVersion)
//            val scalaLib = Path.fromString(projectBootDir, "lib/scala-library.jar")
//            new File( scalaLib, "$INSTALL_PATH/lib")

            val jars = (("lib" +++ "lib_managed") **
                        ("*.jar" - "izpack*.jar"
                                 - "scalatest*.jar"
                                 - "scala-library*.jar"
                                 - "scala-compiler.jar"))  +++
                        (projectBootDir ** "scala-library.jar")
            new FileSet(jars, "$INSTALL_PATH/lib")
        }
      }
    }

    lazy val installer = task {buildInstaller; None}
                        .dependsOn(packageAction, docAction)
                        .describedAs("Build installer.")

    private def buildInstaller = {
        val installerJar = projectName.value.toString.toLowerCase + "-" +
                           projectVersion.value.toString + "-install.jar"
        izpackMakeInstaller(installConfig, "target" / installerJar)
    }
}
