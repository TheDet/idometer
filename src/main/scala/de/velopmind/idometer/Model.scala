package de.velopmind.idometer

import java.util.Date

/*
 * The model for the I-do-Meter application consists of a repository maintaining
 * a set of tasks, where each task represents a topic to work on.
 *
 * Each time span during the day is consumed by one task.
 */



/**
 A task represents the identified item of time consume
 A person works on one task at a time.
 Starting to work on a task stops other running tasks.
*/
case class Task(id:String, 
                descr:String,
                estimatedTime:Duration,
                consumedTime:Duration = Duration(0),
                activities:List[Activity] = Nil,
                finished:Boolean = false
               ) 
{
    implicit def timespan(milisec:Long) = Duration(milisec)  
//	def consume(start:Long, stop:Long):Task = copy(consumedTime = (this.consumedTime + (stop - start)))
//	def consume(start:Date, stop:Date):Task = consume(start.getTime , stop.getTime )
    def consume(activity:Activity) = {
      copy(  consumedTime = (consumedTime + activity.duration),
             activities   = activity :: this.activities          )
    }
    
    def finish = copy(finished = true)
}

/** 
 * An activity represents the work that has been done during a given time span
 * for a specific task.
 * I.e.: When switching between tasks, each finished task will be a list
 * of activities.
 * 
 * When stopping an activity, a text can be stored in 'descr' to report
 * what has been done during the period of this activity.
 */
case class Activity (taskid:String,
                     start:Date = Timestamp(),
                     stop:Option[Date] = None,
                     descr:String = ""
                    )     
{
    def comment(mesg:String) = copy(descr = mesg)
    def finish = copy(stop = Some(Timestamp()))
    /** duration: returns the duration from activity start until stop.
     *  If activity is not finished yet, duration is calculated until 'now' */
    def duration = stop match {
      case Some(stopdate) => Duration( stopdate.getTime - start.getTime )
      case None             => Duration( Timestamp.time - start.getTime)
    }
}




/**
 * This class represents a time span unit
 * The representations 'asHours' and 'asMinutes' are rounded down 
*/
case class Duration (milisec:Long=0) {
    def asHours          = ((milisec / 1000) / 3600) 
    def asMinutes        = ((milisec / 1000) / 60)
    def asTime           = ""+asHours+":"+(asMinutes - asHours * 60)
    def +(that:Duration) = Duration(this.milisec + that.milisec)
}

/**
 *Time (class and companion object) provide helper constructs to
 *declare time intervals in the source code.
 */
class Time(value:Long) {
    def h = value * 3600 * 1000
}

object Time {
    implicit def longToTime(value:Long) = new Time(value)
}

/** This object hides the creation of Date instances and allows
     for testing */
object Timestamp {
   val defaultfactory = () => new Date()
   var datefactory    = defaultfactory  // can be replaced for testing
   def apply() = datefactory()
   def date = datefactory()
   def time = datefactory().getTime
}

/**
 * The repository maintains the data model of tasks and activities
 */
class Repository {
    var allTasks                 = Map[String, Task]()   // Or should it be a List ??
    var allActivities           = List[Activity]()
    var currentTask:Option[Task] = None
    var currentActivity:Option[Activity] = None

    def addTask(t:Task)     { allTasks += (t.id -> t) }
    def makeCurrent(t:Task) { currentTask = Some(t) }
    def startCurrent()      { currentActivity = currentTask match {
                                case Some(t) => Some(Activity(t.id, Timestamp())) 
                                case None    => None
                              }
    //for ( act <- currentActivity) { allActivities +:= act}
    }
    
    def stopCurrent(mesg:String = "")       {
        for (ca <- currentActivity) {
            val fa = ca.comment(mesg).finish
            allActivities +:= fa
//     for (ct  <- currentTask;	 ca <- currentActivity;  val fa = ca.comment(mesg).finish) {
//	        currentTask = Some(ct.consume(ca.comment(mesg).finish))
//	        allTasks += (currentTask.get.id -> currentTask.get)
	}
        currentActivity = None
      //currentTask      = None
    }
}

