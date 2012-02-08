
uilib:	(swt) user-interface-library 
============================

* API-Documentation: [JavaDoc](http://fab1an.github.com/uilib/javadoc/).
* Download **latest** build: [uilib.jar](http://fab1an.github.com/uilib/uilib.jar).

There's a sample application in the source which shows some of the features.

Idea
-------
Uilib is a collection of utilities aiming to improve and help coding with SWT and
building better, more comprehensive, modern applications.
It's a library, not a framework in the sense, that it tries not to force you to change your
application-structure. Instead you can gradually adapt your code to use parts and features,
which you might find useful.

A lot of the API was designed with the special circumstances of SWT-GUI-applications in mind.
Almost all SWT-related calls have to be done in the Display-Thread, which enables you to use static methods more freely.
The library makes heavy use of the Google Guava-toolkit, which enables you to write more modern and fun Java-code.
A lot of the code is safe-guarded by using Guava's Preconditions which makes the code fail-fast on illegal arguments etc.

I'm also a big fan over convention over configuration and i think it's ok to give up type-safety and do some dynamic casting
from time to time.

Dependencies
------------------------

It should work with older versions of libraries as well.

* [gson-2.0](http://google-gson.googlecode.com) fast json parsing library
* [guava-10.0.1] (http://guava-libraries.googlecode.com/) google's core java-libraries
* [jna-3.4] (https://github.com/twall/jna) java native access, only necessary to get correct ApplicationData Folder on Windows
* [slf4j-api-1.6.4](http://slf4j.org/) simple logging facade for java, provides pluggable logging *static binding, no need for configuration)
* [swt-3.7.1](http://www.eclipse.org/swt) SWT

License
-------------
Code is released under the LGPL.
CocoaUIEnhancer.java is an exception, it's released under the EPL.

Thank you
-----------------
If you want you can [flattr](https://flattr.com/profile/cel1ne) me.

Features / Overview
-------------------------------
> ## Templating:
> Load interface descriptions from json
> Work width interface-components by selecting elements via a query syntax
> ## EventHandling:
> Simple wrappers to write less cluttered event-handling code using Guava's EventBus
> ## Utilities:
> Registries for handling Colors, Fonts and Images
> Store and load user-preferences
> Throttle Runnables
> Display overlays on Composites
> Do measurements of your code run-time
> …
> ## Various widget utilities:
> Automatically resize table columns
> save / restore column-order and weights
> save / restore Shell position, maximised state etc.
> ScrollListener for Table
> …
> ## Various useful widgets
> better MessageBox
> SearchFrom
> better SaveFileDialog

RFC / Advise
------------------------
* Tell me if i overlooked something concerning licensing
* **Component** does it make sense to select something before initialization
* MigLayout instead of GridLayout?
* Where does a BuilderSyntax make sense? e.g. options.get("bold").withDefault(false);

TODOs / Ideas
------------------------

> ### General: ###
> * Help for (Win)Sparkle-Integration
> * Unit tests
> * Help for creating browser-based widgets (links and images are a problem)
> * Help to intergrate swing-widgets

> ### Overlay: ###
> * paint directly on widget without the need of a shell
> * ability to lock composite

> ### Application: ###
> * provide a wrapper to run background tasks and handle threading (weak references?)
> * Executor that executes Runnable's in the display thread on a best effort base by 
> collection event-statistics

> ### Templating ###
> * different format (yaml?)
> * MigLayou?
> * LayoutUI that positions widget absolute
> Template editing help:
> > * fast reloading of templates
> > * activate all composite borders
> > * gridlayout configuration
> > * write back json to format it properly
	
> ### Measurement / Statistic 
> summary output (longest running calls etc.)
> wrapper that measures the length of swt-event-handlers

> ### Widgets
> * Table that shows results fast
> * MBox: work on it so it's Mac-like on the mac
> * typical "+","-" buttons for the mac
> * debugviewer
> * LicenseViewer for linked opensource-libraries
> * ProgressBar with soft animation
