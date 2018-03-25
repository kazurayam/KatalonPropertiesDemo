A proposal to Katalon Studio: katalon.properties file in multiple locations
=====================================================================================

# What it is

This is a [Katalon Studio](https://www.katalon.com/katalon-studio/) project for demonstration purpose.

# `katalon.properties` spec
Here I introduce my custome Groovy class `com.kazurayam.KatalonProperties`. Once instanciated, `KatalonProperites` object tries to find `katalon.properties` file. Multiple locations are searched in the following order. If a single property is configured in multiple l locations the last one wins:

1. `<runtime current directory>/katalon.properties` is loaded if exists
2. `$HOME/katalon.properties` for Mac/Linux, `%USERPROFILE%\katalon.properties` for Windows, is loaded if exists
3. If JVM System Property `katalon.user.home` is set with any directory path, then
   `new File(System.getProperty('katalon.user.home') + '/katalon.properties')` is loaded if exists

I use `com.kazurayam.KatalonProperties` class in my Test Listener `TL_Run`. In the Test Listeners's method annoteded as `@BeforeTestSuite`, I override `GlobalVariables.hostname` with value found in my `$HOME/katalon.propertes`.

Effectively I can switch the URL(hostname) of AUT by slightly modifying `$HOME/katalon.propertes` while the projects GlobalVariables unchanged.

`com.kazurayam.KatalonStudio` class enables you to specify values of GlobalVariables runtime without exposing them visible in the public Git repositories.

The 3rd option -- JVM System Properties `katalon.user.home` would enable us, without modifying the source code of Katalon project, apply a single Katalon Test Suite to multiple environments of a Web Application; for example Develoment, Staging, Production-A, Projection-B, Production-C and more. Howerver [Katalon Studio console mode execution](https://docs.katalon.com/display/KD/Console+Mode+Execution) does not allow us to  specify JVM System Property as commandline argument. So the 3rd option is actually not operational. I would request Katalon Team to consider this.

# Those who may concern

This project may be helpful for the issues raised by the following discussions on Katalon Studio:

- [Is it possible to use the same test for different sites?](https://forum.katalon.com/discussion/5689/is-it-possible-to-use-the-same-test-for-different-sites#latest)
- [Want to overwrite credential info as GlobalVariable with properties file from commandline argument](https://forum.katalon.com/discussion/5362/want-to-overwrite-credential-info-as-globalvariable-with-properties-file-for-commandline-argument)
