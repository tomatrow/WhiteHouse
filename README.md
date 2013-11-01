# White House #
An interactive fiction mapping program.

## What is it? ##

### What is interactive fiction? ###
You can think of interactive fiction as an RPG made from only text. In a typical IF, the game file simulates an environment or story and the player uses commands to interact or change it. Some common commands might be `get apple`, `drop apple`, or `north`. Most IFs are a type of puzzle with a goal such as "find all treasure" or "escape from the house".

### What is a mapping program? ###
Interactive fiction is implemented in a series of rooms with connections at the compass points (north, northeast etc.) in addition to up and down. A mapper helps the player keep track of where he is and how to get where he needs to go.

## Features ##
* **Auto mapping**  
Automatically builds a map by reading the game transcript as you play the game.

* **Handles multi-floor maps**  
Navigate maps floor by floor, or view with the 3d interface. (3d not yet implemented.)

* **Native cross-platform GUI**  
Built using Standard Widget Toolkit which wraps native widgets in java code. In other words, White House will look, feel, and act like the rest of your system.


## Build and run ##

There are not currently any binaries built, but compiling is very simple. The only dependence you will need is Java JDK 7. Included with the source is two build scripts: *build.bat* for windows and *build.sh* for linux and mac (will not work with Cygwin).

To compile, execute `./build compile` from the root project directory. The script will automatically select the correct swt library for your system.

To run, simply execute `./build run`.
