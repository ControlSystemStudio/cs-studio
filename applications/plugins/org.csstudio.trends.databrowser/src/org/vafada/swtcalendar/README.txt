Got this from swtcalendar.sourceforge.net,
and changed some List/ArrayList uses
to ArrayList<specific type> to eliminate warnings.

-Kay

----- Original README.txt

Introduction

SWTCalendar is a port of Kai Toedter's JCalendar to Eclipse's SWT. 
It is a GUI date picker for Java using SWT as the GUI toolkit. 
SWTCalendar was designed to be a flexible component so developer can 
embed a date picker in their application or create their own standalone date picker dialog.

Compile and Building

A build file is included in the distribution of SWTCalender. 

It is required that you have ANT installed. Create a "lib" dir and put your copy of swt.jar in the "lib" directory.
You can name your own directory on where you want the swt.jar to reside as long as you modify the Ant script to reflect
your directory structure. Copy the required swt DLL file on the root directory.

The "jar" target builds the jar file from source.

Demos

"demo1" and "demo2" target of ant runs the examples included distribution.

Installation

Just put swtcalendar.jar in your classpath :)
