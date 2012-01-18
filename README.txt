Getting Started

* Eclipse Installed

  You need Eclipse installed.  I'm not sure what version is Ok.  I know Eclipse 3.6 works.

** Java and PDE features

  You need the Java and PDE features installed to work on Hastee

** Hastee dependencies installed

  The easiest way is to also install the latest version of Hastee into your Eclipse as this should pull down dependencies.

  You may have to manually install some like XText 2.0.0 from
    http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/

** EGit Installed.

  You need EGit (http://eclipse.org/egit/) for version control.

* Forking the Hastee Project on SourceForge Git

  Browse to the project's Git repository at https://sourceforge.net/p/hastee/git/

  Click the "Fork" menu button on the left hand panel.

  Select "u/<username" for Project and "hastee" for Repository Name and click the "Fork" button.

* Creating EGit Repository for Hastee

  Having created your fork about there is now an item under "Forks" for "<username>/hastee", click this item.
  It should reference https://sourceforge.net/u/<username>/hastee/

  I use the HTTP option to access the git repository, so click on the HTTP button will give git clone command:
    git clone https://<username>@git.code.sf.net/u/<username>/hastee u-<username>-hastee

  In Eclipse open the "Git Repository Exploring Perspective"; Window > Open Perspective > Other... > Git Repository Exploring

  Copy just the https link from your fork and paste it into the Git Repositories window. (i.e. https://<username>@git.code.sf.net/u/<username>/hastee)

  This will bring up the "Clone Git Repository", fill in your password and click Next.
  On the "Branch Selection" screen the default "master" branch is fine, click Next.
  On the "Local Destination" screen you may want to change the Directory to be "u-<username>-hastee" instead of hastee, click Next.
  Ignore the Gerrit Code Review screen and click Finish.
  See the EGit documentation for more information.

  This will setup your Git repository for Hastee.

* Creating the Eclipse Project for Hastee

  From the Java Perspective, select File > Import ... > Git > Projects from Git, click Next.

  Select the Git Repository created above, "u-<username>-hastee", click Next.

  The default selection "Import as general project" is fine, click Next.

  The default Project name should be ok "u-<username>-hastee", click Finish.

** Importing the Hastee Project

  This is a bit black magic, because Eclipse doesn't like to import projects that have a parent location that is already in the workspace.
  If you do attempt this you will see an error message "Some or all projects can not be imported because they already exist in the workspace".
  To work around this, open the Navigator view; Window > View > Navigator.
  Find the .project file in the root of the project and delete it.  Don't worry Eclipse will recreate this file for us.

  Now do the import steps again:

  From the Java Perspective, select File > Import ... > Git > Projects from Git, click Next.

  Select the Git Repository created above, "u-<username>-hastee", click Next.

  Select "Import existing projects", click Next.

  This will find the Hastee Eclipse PDE projects:
    * net.sf.hastee
    * net.sf.hastee.feature
    * net.sf.hastee.site
    * net.sf.hastee.test
    * net.sf.hastee.ui
  (Remember if you get the error message "Some or all projects can not be imported because they already exist in the workspace" follow the instructions above)

  Click Finish.

* Building Hastee

** Generate the XText files.

  Find net.sf.hastee/src/net.sf.hastee/ST.xtext

  Right click and select Run As > Generate Xtext Artifacts.

* Trouble Shooting

** Create a User Library

  Window > Preferences > Java > Build Path > User Libraries

  Click New...

  Give the User library name e.g "log4j" and click Ok.
  (Note: Enabling "System library" doesn't avoid setting this in the run configuration)

  Select the user library and click "Add JARs..." browse to your the jar file to include.
  e.g. In the Eclipse installation directory/plugins and org.apache.log4j_<version>.jar

  Click Ok to dismiss the preferences window.

** Add a User Library to a Run Configuration

  The workaround is to open the Run > Run Configurations ... > Mwe2 Launch > GenerateST.mwe2 and click on the Classpath tab.
  Click User Entries and then the Advanced... button, select Add Library and click Ok.
  Select User Library and click Next.
  Select the log4j user library (that you added earlier) and click Finish.


** Generating XText fails with java.lang.NoClassDefFoundError: org/apache/log4j/Logger

  See https://bugs.eclipse.org/bugs/show_bug.cgi?id=361202

+--
java.lang.NoClassDefFoundError: org/apache/log4j/Logger
  at org.eclipse.emf.mwe2.launch.runtime.Mwe2Launcher.<clinit>(Mwe2Launcher.java:31)
Caused by: java.lang.ClassNotFoundException: org.apache.log4j.Logger
  at java.net.URLClassLoader$1.run(URLClassLoader.java:202)
  at java.security.AccessController.doPrivileged(Native Method)
  at java.net.URLClassLoader.findClass(URLClassLoader.java:190)
  at java.lang.ClassLoader.loadClass(ClassLoader.java:307)
  at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:301)
  at java.lang.ClassLoader.loadClass(ClassLoader.java:248)
  ... 1 more
Exception in thread "main"
+--

  Google was no help in finding an answer to this problem.

  The workaround is to Add a User Library to a Run Configuration for the log4j library.

** Generating XText fails with "Connection refused: connect" when trying to download http://download.itemis.com/antlr-generator-3.2.0.jar

  The cause is probably because you are behind a firewall

+--
0    [main] INFO  lipse.emf.mwe.utils.StandaloneSetup  - Registering platform uri '...'

*ATTENTION*
It is recommended to use the ANTLR 3 parser generator (BSD licence - http://www.antlr.org/license.html).
Do you agree to download it (size 1MB) from 'http://download.itemis.com/antlr-generator-3.2.0.jar'? (type 'y' or 'n' and hit enter)y
237145 [main] INFO  erator.parser.antlr.AntlrToolFacade  - downloading file from 'http://download.itemis.com/antlr-generator-3.2.0.jar' ...
238161 [main] ERROR erator.parser.antlr.AntlrToolFacade  - Connection refused: connect
[del]
+--

  Why this isn't bundled with XText is beyond me, its part of the license conditions http://www.antlr.org/license.html

  The workaround is to manually download the file and to create a "Create a User Library" and then "Add a User Library to a Run Configuration"
  I installed the downloaded file into the root of my Eclipse installation.



