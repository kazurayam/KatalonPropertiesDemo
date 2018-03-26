Demonstrating how katalon.properties file enhances usability of Katalon Studio
==============

# What is this?

This is a simple [Katalon Studio](https://www.katalon.com/katalon-studio/) project for demonstration purpose.

This shows you how I overwrite the GlobalVariables in my Katalon project with values loaded from `katalon.properties` files. The `katalon.properties` files can be located in multiple locations outside the project. You can exclude them from Git repository. Hence I can hide my sensitive information (hostname, credentials etc) even if I put the project publicly shared at GitHub.

# Problems to solve

Let me assume, I have a single Web Application in multiple environments: the development, the staging, the production-blue, the production-green and more. These environments have 99% same contents and features except the following differences:

+ Hostname --- e.g. [`demoaut-mimic.kazurayam.com`](http://demoaut-mimic.kazurayam.com) for development, [`demoaut.katalon.com`](http://demoaut.katalon.com/) for production
+ Username/Password --- e.g. `kazurayam/foobar` for development, `John Doe/ThisIsNotAPassword` for production.

I want to do as follows:

1. I want to use [Katalon Studio](https://www.katalon.com/) to test Web UI of all these environments using a single set of Test Suites/Test Cases.
3. I am quite willing to make code changes while targeting to the development environment. But I am reluctant to make changes in order to switch targets.
4. I want to store the Katalon project into the GitHub and to expose it public (just as I did [here](https://github.com/kazurayam/KatalonPropertiesDemo)).
5. Still I want to hide my sensitive information: hostname, username and password. I do not make them visible anywhere in the repository.

# How to solve it

Here I propose to introduce `katalon.properties` file as a method to configure a [Katalon Studio]() project runtime.

# How I solved it

+ In the Katalon project, I made a set of `GlobalVariable`s for hostname, username and password. I gave value of empty strings("") as default. This  will be stored and exposed public via Git.
+ On my PC, I can create `katalon.properties` files in [`java.util.Properties`](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) format in various locations as the following list shows. If a single Property is declared in multiple locations, the last wins:
    1. `<current directory>/katalon.properties` is loaded if exists
    2. `$HOME/katalon.properties` on Mac/Linux, `%USERPROFILE%\katalon.properties` on Windows is loaded if exists
    3. If JVM System Property `katalon.user.home` is provided, then the file `katalon.properties` under the directory `new File(System.getProperty("katalon.user.home"))` is loaded.
+ I haved developed a Groovy class `com.kazurayam.KatalonProperties`. The code is avaiable [here](https://github.com/kazurayam/MultiSourcedProperties). This class is capable of loading properties from katalon.properites files. This class is contained in `MultiSourcedProperties-1.0.jar` which you can download [here](https://github.com/kazurayam/MultiSourcedProperties/raw/master/build/libs/MultiSourcedProperties-1.0.jar).
+ This demo project ([`KatalonProperitesDemo`](https://github.com/kazurayam/KatalonPropertiesDemo)) depends on `MultiSourcedProperties-1.0.jar`, so you need to download the jar onto your local PC, configure your Katalon Studio project to refer to the downloaded jar. Do `Project > Settings > External Libraries > Add`
+ I made a [`Test Listener`](https://docs.katalon.com/pages/viewpage.action?pageId=5126383) in the demo project. In the [method annotated with `@BeforeTestSuite`](https://github.com/kazurayam/KatalonPropertiesDemo/blob/master/Test%20Listeners/TL_Run.groovy), it instanciates a  KatalonProperties object which loads ./katalon.profiles and $HOME/katalon.properties on startup. The Test Listener overwrites the `GlobalVariable.hostname` with value picked up from external `katalon.properties` file.
+ Once overridden, the new value of `GlobalVariable.hostname` is refered to throughout the Test Suite run.

# Proposal to Katalon Studio

I want to specify JVM System Property `katalon.user.home` as command line arguments for Katalon. For example I want to test the production Blue environement by typing in the command line like this:
```Console
>cd %KatalonStudioInstalledDir%
>.\katalon.exe -Dkatalon.user.home=C:\Users\myname\tmp\blue -runMode=console -noExit -projectPath="C:\Users\myname\katalon-workspace\KatalonPropertiesDemo\KatalonPropertiesDemo.prj" -testSuitePath="Test Suites/TS_Run" -browserType="Firefox"
```
Here I intruduced an argument in the format of `-Dkatalon.user.home=xxxxxx`. I mean this argument adds a System Property named `katalon.user.home` to JVM. This property will be available to any running Groovy code within the project.

Here I mean the `katalon.user.home` system property stands for the location of katalon.properties file to be loaded. In this case `C:\Users\myname\tmp\blue\katalon.properties` file should be loaded by the Test Listener.

Next I want to test the production Green environment like this:
```Console
>.\katalon.exe -Dkatalon.user.home=C:\Users\myname\tmp\green -runMode=console -noExit ...
```
In this case `C:\Users\myname\tmp\green\katalon.properties` file should be loaded by the Test Listener.


Finally I want to test the staging environment like this:
```Console
>.\katalon.exe -Dkatalon.user.home=C:\Users\myname\tmp\staging -runMode=console -noExit ...
```
In this case `C:\Users\myname\tmp\staging\katalon.properties` file should be loaded by the Test Listener.

I think that having `-Dkatalon.user.home=XXXXXXXXX` argument enhances the usability of Katalon Studio. With it I can switch the AUT just by typing different location of `katalon.properties` file as a command line argument; while no modification in the project's code set required.

However the Katalon Studio v5.3.1 [Console Mode Execution](https://docs.katalon.com/display/KD/Console+Mode+Execution) __does NOT accept arguments to add JVM System Properties.__ I want the  Katalon Team to consider adding this.

# Related Katalon discussions

In the [Katalon Discussion Forum](https://forum.katalon.com/discussions) I found a few discussions on test reuse and hiding credentials.

- [Is it possible to use the same test for different sites?](https://forum.katalon.com/discussion/5689/is-it-possible-to-use-the-same-test-for-different-sites#latest)
- [Want to overwrite credential info as GlobalVariable with properties file from command line argument](https://forum.katalon.com/discussion/5362/want-to-overwrite-credential-info-as-globalvariable-with-properties-file-for-commandline-argument)

I hope my study suggests something informative to those who may concern.
