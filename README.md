Demonstrating how katalon.properties file enhances usability of Katalon Studio
==============

# What is this?

This is a simple [Katalon Studio](https://www.katalon.com/katalon-studio/) project for demonstration purpose. You can check this out onto your PC and execute with your Katalon Studio.

This shows you how I overwrite the GlobalVariables in my Katalon project with values loaded from `katalon.properties` files. The `katalon.properties` files can be located in multiple locations outside the project. I can exclude them from Git repository. Hence I can hide my sensitive information (hostname, credentials etc) even if I put the project publicly shared at GitHub.

# Problems to solve

Let me assume, I have a single Web Application in multiple environments: the development, the staging, the production-blue, the production-green and more. These environments have 99% same contents and features except the following differences:

+ Hostname --- e.g. [`demoaut-mimic.kazurayam.com`](http://demoaut-mimic.kazurayam.com) for development, [`demoaut.katalon.com`](http://demoaut.katalon.com/) for production
+ Username/Password --- e.g. `kazurayam/foobar` for development, `John Doe/ThisIsNotAPassword` for production.

Now I want to:

1. I want to use [Katalon Studio](https://www.katalon.com/) to test Web UI of all these environments using a single set of Test Suites/Test Cases.
2. I want to store the Katalon project into the GitHub and to expose it public (just as I did [here](https://github.com/kazurayam/KatalonPropertiesDemo)).
3. Still I want to hide my sensitive information: hostname, username and password. I do not like making them visible anywhere in the repository.
4. I want to run the test against multiple targets (hostnames) in Continuous Integration process on Jenkins. In order to do this, I need to be able to switch the test target by command line argument without modifying the source code of the test at all.

# How to run the demo

1. git clone [this demo project](https://github.com/kazurayam/KatalonPropertiesDemo) to your PC.
2. Start Katalon Studio and open the downloaded project.
3. This demo project depends on an external jar `MultiSourcedProperties-1.0.jar` which you can download from  [here](https://github.com/kazurayam/MultiSourcedProperties/raw/master/build/libs/MultiSourcedProperties-1.0.jar). You need to download it and [configure the Katalon Studio project to refer to the external lib](https://docs.katalon.com/display/KD/External+Libraries). Do `Project > Settings > External Libraries > Add` operation.
4. You want to create a file `%USERPROFILE%\katalon.properties` for Windows, `$HOME/katalon.properties` for Mac. The file should contain following line:
```
GlobalVariable.hostname=demoaut.katalon.com
```
5. Select the Test Suite `TS_Run` and run it with Firefox.
6. In the Log Viewer you will find INFO lines like this:
```
Starting invoke 'com.kms.katalon.core.annotation.BeforeTestSuite' method: 'TL_Run.sampleBeforeTestSuite(...)'
>>> GlobalVariable.hostname default value: ''
>>> GlobalVariable.hostname new value: 'demoaut.katalon.com'
```
These lines indicates that the `GlobalVariable.hostname` initially had empty value, and was changed by the Test Listener with new value loaded from `katalon.properties` file you created.

# How I solved it

Here I propose to introduce `katalon.properties` file as a method to configure a [Katalon Studio]() project runtime. Please check the codes of [this demo project](https://github.com/kazurayam/KatalonPropertiesDemo) as you read through the following descriptions.

+ In the demo project, I made a set of `GlobalVariable`s for hostname, username and password. I gave value of empty strings("") as default. This  will be exposed public via Git.
+ On my PC, I can create `katalon.properties` files in [`java.util.Properties`](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) format in various locations as the following list shows. If a single Property is declared in multiple locations, the last wins:
    1. `<current directory>/katalon.properties` is loaded if exists
    2. `$HOME/katalon.properties` on Mac/Linux, `%USERPROFILE%\katalon.properties` on Windows is loaded if exists
    3. If JVM System Property `katalon.user.home` is given, then a `katalon.properties` file under the directory `new File(System.getProperty("katalon.user.home"))` is searched and loaded.
+ I have developed a Groovy class `com.kazurayam.KatalonProperties`. The code is avaiable [here](https://github.com/kazurayam/MultiSourcedProperties). This class is capable of loading properties from multiple locations as described above. This class is contained in the  [`MultiSourcedProperties-1.0.jar`](https://github.com/kazurayam/MultiSourcedProperties/raw/master/build/libs/MultiSourcedProperties-1.0.jar).
+ I made a [`Test Listener`](https://docs.katalon.com/pages/viewpage.action?pageId=5126383) in the demo project. In the [method annotated with `@BeforeTestSuite`](https://github.com/kazurayam/KatalonPropertiesDemo/blob/master/Test%20Listeners/TL_Run.groovy), it instanciates a  KatalonProperties object which loads ./katalon.profiles and $HOME/katalon.properties on startup. The Test Listener overwrites the `GlobalVariable.hostname` with the value picked up from external file.
+ Once overridden, the new value of `GlobalVariable.hostname` is refered to throughout the Test Suite run.

Here I confess that the design of runtime-configuration using properites file comes from [gradle.properties](https://docs.gradle.org/4.6/userguide/build_environment.html#sec:gradle_configuration_properties).


# Proposal to Katalon Studio

I hope I can specify JVM System Property `katalon.user.home` as command line arguments for the Katalon Studio in Console Mode. For example, I would type like this if I want to test the production Blue environement:
```Console
>cd %KatalonStudioInstalledDir%
>.\katalon.exe -Dkatalon.user.home=C:\Users\myname\tmp\blue -runMode=console -noExit -projectPath="C:\Users\myname\katalon-workspace\KatalonPropertiesDemo\KatalonPropertiesDemo.prj" -testSuitePath="Test Suites/TS_Run" -browserType="Firefox"
```
Here I typed an argument `-Dkatalon.user.home=xxxxxx`. I mean this argument adds a JVM System Property named `katalon.user.home`. This property will be available to any running Groovy code within the project.
I learned the argument `-Dnnnnn=xxxxx` from [the good-old `java` command](https://www.ibm.com/support/knowledgecenter/en/SSYKE2_7.0.0/com.ibm.java.win.70.doc/user/specifying_options.html).

Here I mean the `katalon.user.home` system property stands for the location of katalon.properties file to be loaded. In this case `C:\Users\myname\tmp\blue\katalon.properties` file should be loaded by the Test Listener.

Next I want to test the production Green environment by typing like this:
```Console
>.\katalon.exe -Dkatalon.user.home=C:\Users\myname\tmp\green -runMode=console -noExit ...
```
In this case `C:\Users\myname\tmp\green\katalon.properties` file should be loaded by the Test Listener.


Finally I want to test the staging environment by typing like this:
```Console
>.\katalon.exe -Dkatalon.user.home=C:\Users\myname\tmp\staging -runMode=console -noExit ...
```
In this case `C:\Users\myname\tmp\staging\katalon.properties` file should be loaded by the Test Listener.

I would like to emphasize that `-Dkatalon.user.home=XXXXXXXXX` argument would enhance the usability of Katalon Studio significantly. Provided with this feature I can switch the AUT just by typing different location of `katalon.properties` file as a command line argument, while no modification in the project's code set required at all. This feature will make it easy to run Katalon Studio in Continuous Integration processes in Jenkins targeting multiple hosts. This will make Katalon Studio a good tool applicable to [Blue-Green deployment](https://martinfowler.com/bliki/BlueGreenDeployment.html).

I am aware that Katalon Studio v5.3.1 [Console Mode Execution](https://docs.katalon.com/display/KD/Console+Mode+Execution) does NOT accept the `-Dnnnn=xxxx` arguments. I home Katalon Team to consider adding this feature.

# Related discussions

In the [Katalon Discussion Forum](https://forum.katalon.com/discussions) I found quite a few discussions on test reuse, passing parameters to automated tests, and hiding credentials.

- [Is it possible to use the same test for different sites?](https://forum.katalon.com/discussion/5689/is-it-possible-to-use-the-same-test-for-different-sites#latest)
- [Automate running a test from maven (as part of build process)](https://forum.katalon.com/discussion/5696/automate-running-a-test-from-maven-as-part-of-build-process#latest)
- [How to pass user defined parameters from command line](https://forum.katalon.com/discussion/4586/how-to-pass-user-defined-parameters-from-command-line#latest)
- [How to pass the the parameter to the Test listener method..](https://forum.katalon.com/discussion/5490/how-to-pass-the-the-parameter-to-the-test-listener-method#latest)
- [Want to overwrite credential info as GlobalVariable with properties file from command line argument](https://forum.katalon.com/discussion/5362/want-to-overwrite-credential-info-as-globalvariable-with-properties-file-for-commandline-argument)
- [How to change site based on environmet?](https://forum.katalon.com/discussion/5366/how-to-change-site-based-on-environmet#latest)
- [Inject data with external file?](https://forum.katalon.com/discussion/5352/inject-data-with-external-file#latest)
- [Possibility to send property/variable through Katalon console cmd line](https://forum.katalon.com/discussion/4906/possibility-to-send-property-variable-through-katalon-console-cmd-line#latest)
- [Store variables like username and password](https://forum.katalon.com/discussion/5271/store-variables-like-username-and-password#latest)
- [Variable URL](https://forum.katalon.com/discussion/5034/variable-url#latest)
- [How to pass user defined parameters from command line](https://forum.katalon.com/discussion/4586/how-to-pass-user-defined-parameters-from-command-line#latest)
- [Passing variables from jenkins to katalon scripts](https://forum.katalon.com/discussion/4152/passing-variables-from-jenkins-to-katalon-scripts#latest)
- [pass global variable value to Katalon in CMD line](https://forum.katalon.com/discussion/2125/pass-global-variable-value-to-katalon-in-cmd-line#latest)
- [Maintain different environment properties file and run in multiple environment same time](https://forum.katalon.com/discussion/4983/maintain-different-environment-properties-file-and-run-in-multiple-environment-same-time#latest)

I hope my study suggests something useful to those who may concern.
