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
    <task id ={task.id.toString}>
    <title>{task.title}</title>
    <descr>{task.descr}</descr>
    <estimated>{task.estimatedTime.milisec}</estimated>
    <finished>{task.finished}</finished>
    </task> 
  
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
   
  def loadRepo(filename:String) = {
      xmlToRepo(scala.xml.XML.load(filename))
  }
  
  def xmlToRepo(n:Node) = {
      val repo = new Repository()
      repo.allTasks      = xmlToTasks(n)
      repo.allActivities = xmlToActivities(n)
      repo
  }
  
  def xmlToActivities(n:Node):List[Activity] = ( (n \ "activity") map {xmlToActivity} ).toList
  
  def xmlToTasks(n:Node):Taskmap =
     ((n \ "task") map {xmlToTask}).toList.foldLeft(new Taskmap()) {(m:Taskmap,t:Task) => m + (t.id -> t)}
  
  
  def xmlToTask(n:Node)     = Task(id = (n \ "@id").text.toInt,
                                   title = (n \ "title").text,
                                   descr = (n \ "descr").text,
                                   estimatedTime = Duration((n \ "estimated").text.toLong),
                                   finished = (n \ "finished").text.toBoolean
                              ) 
  
  def xmlToActivity(n:Node) = Activity(
                                    (n \ "taskid").text.toInt,
                                    new Date((n \ "start").text.toLong),
                                    textToDateOption((n \ "stop").text),
                                    (n \ "desc").text
                              )
                              
  def textToDateOption(s:String) = s match {
        case x:String if x.length > 0 => Some(new Date(x.toLong))
        case _  => None
  }
}
