/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.velopmind.idometer.xml

import de.velopmind.idometer._
import scala.xml._

import java.util.Date

class Persistence {
  
  def repoToXML(repo: Repository) = 
    <repository>
    {for (t <- repo.allTasks) yield taskToXml(t._2) }
    {for (a <- repo.allActivities) yield activityToXml(a)}
    </repository>
    
  def taskToXml(task:Task) = 
    <task id ={task.id}>
    <descr>{task.descr}</descr>
    <estimated>{task.estimatedTime.milisec}</estimated>
    <finished>{task.finished}</finished>
    /task> 
  
  def activityToXml(act:Activity) = 
    <activity>
    <taskid>{act.taskid}</taskid>
    <start>{act.start.getTime}</start>
    { act.stop match {
        case Some(d) => <stop>{d.getTime}</stop>
        case None     => <stop />
    }}
    <desc>{act.descr}</desc>
    </activity> 

   def saveRepo(filename:String, repo:Repository) {
       scala.xml.XML.save(filename, repoToXML(repo))
   }
   
  def loadRepo(filename:String) {
      val repoNode = scala.xml.XML.load(filename)
  }
  
  def xmlToRepo(n:Node) = {
      val repo = new Repository()
      repo.allTasks = xmlToTasks(n)
      repo.allActivities = xmlToActivities(n)
  }
  
  def xmlToActivities(n:Node):List[Activity] = ( (n \ "activity").map { a => xmlToActivity(a)}).toList
  
  def xmlToTasks(n:Node) = Map[String, Task]()
  
  
  def xmlToTask(n:Node)     = Task(id = (n \ "@id").text,
                                   descr = (n \ "descr").text,
                                   estimatedTime = Duration((n \ "estimated").text.toLong),
                                   finished = (n \ "finished").text.toBoolean
                              ) 
  
  def xmlToActivity(n:Node) = Activity(
                                    (n \ "taskid").text,
                                    new Date((n \ "start").text.toLong),
                                    Some(new Date((n \ "stop").text.toLong)),
                                    (n \ "desc").text
                              )
}
