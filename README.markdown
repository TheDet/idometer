I-do-Meter
==========

... is your tool to regain control over your daily work time.

- Ever wondered where all your time has gone?
- Planning and estimating tasks with efforts but running out of time nevertheless? 
- Ever asked yourself "what the * do I do all the time?"

That is, where I-do-Meter joins in.
It is your daily stopwatch and -applied consequently- shows you
- how much time you spent for handling emails
- how often and how long you are interrupted by phone calls
- how often you are obliged to switch tasks (and therefore contexts)
- how much your actual efforts differ from your estimations.

Using it consequently, you will say:
- "Now I know, what I do all the time"
- "I see, how long I do the things"
- "I learn, that I do need more time for some tasks than I thought"
- "Hey, I notice, that often I do not work on the most important tasks"

Try your I-do-Meter - it's free!

So far for the marketing talk ;-)

Basic Concepts
--------------

The idea is, that you create some "tasks" in I-do-Meter, including
the non-domain stuff, like reading emails or drinking coffee, or what else.
You can also set an estimated duration, i.e. how long you think you need
for a task to finish.

Now you start the day by selecting a task and starting it.
The continuous work on one task is called an "activity".
Everytime you change your task (because of an interruption, a pause,
or because your current task goes into wait state), the current
activity is stopped, stored, and a new activity for the new task
is opened.

In the end you have a record of every activity (and indeed every
switch) for a day, week, month ...

As every activity is dedicated to a task, you can now build
reports with sums for every task over some range of time.
A week, a month, a release cycle, whatever. 

Current Status
--------------
The project has a first output: There is an izpack installer to install
version 0.0.3, which is a little test scenario.
You can start a Swing Gui, create new Tasks, select them, and start and
stop activities for them.

The info can be stored in a file and reloaded from it for further handling.
There is even a simple Option dialog, but without any meaning currently.

So what you can do is tracking your tasks. But currently you are not able
to get any data out of it. This is what comes next.


Development
-----------
I-do-Meter is a little hobby project, written in Scala, and serves as a
little example for a Scala tool chain.
(SBT, ScalaTest, scala-swing, Netbeans7.0 Scala Plugin, sbt-netbeans-plugin, sbt-izpack-plugin)

 
