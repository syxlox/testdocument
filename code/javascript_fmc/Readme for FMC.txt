FMC Workitem Save 1.3
---------------------

DESCRIPTION
===========

This extension for Polarion makes it possible to easily run your own script files before and after saving or creating a workitem, so you have a powerful possibility to customize a lot of things to your needs. It's like the polarion workflow function but provides even more possibilities.

You can do a lot with this extension with just a few lines of code. You can find the following scripts as example delivered with this extension:

* Automatically append a prefix to a workitem title if the user has saved or created a new workitem (title-prefix)
* Check the workitem description for weak words and deny saving the workitem if those weak words are found (weak-words)
* Set custom fields automatically if some conditions are met (for example workitem description contains specific words) (auto-severity)
* Import/ Export informations from external services (for example a php site) if a workitem was created (http-import)
* Link workitem to other workitems or create hyperlinks if some conditions are met (create-hyperlink)
* Deny editing some fields (severity, priority ...) within the workitem after the workitem was created (field-lock)
* Deny/ Allow saving or creating workitems or fields of workitems for different reasons and independent from project settings (Saving/ Creating only allowed between 6:00am and 6:00pm, limit or exclude users which can save/ create) (custom-perms)
* Add automatically the current user who has saved/ edited the workitem as watcher, if the workitem could be saved successfully (auto-watcher)
* After a workitem was successful saved, create/ update a hyperlink to a wiki page with status informations about the current workitem status (status-hyperlink)
* ... Much more!

Features:

* You can edit your scripts on the fly without having to restart Polarion. You edit or create a script and its instantly active if you perform the next workitem save.
* Choose whether you want to perform an action before the workitem will be saved (pre-hook with the option to deny saving) or after the workitem was successful saved.
* The scripts will be executed if the user saves the workitem at the gui, but also if you perform save() in a wiki page or if you want to save a workitem via the web service.
* You can specify user friendly error messages ("Title must be at least 10 characters long"), which will be displayed to the user if you deny saving a workitem.

REQUIREMENTS
============

The extension was developed for and tested with:
* Polarion 2011
* Polarion 2012
* Polarion 2012 (SR1)

INSTALLATION
============

You can install this extension within a minute, it's quite easy:

* Copy the extension file "com.polarion.fme.workitemsave.actions_X.X.X.jar" to your existing "Polarion\polarion\extensions\userextensions\eclipse\plugins" folder.
	* The "extensions" folder should already exist, the "userextensions" (you can choose any name for this folder), "eclipse" and "plugins" folder must be created if not exist.
* Create two new folders: "Polarion\scripts" (if not exist) and within this folder a new folder "Polarion\scripts\workitemsave". For the moment this folder is empty, we will fill it with scripts later.
	* Note that you have to create the "scripts" and "scripts\workitemsave" folders within the top most "Polarion" folder (where you can also find the folders "data" and "bundled" but not "plugins")
* Restart Polarion, in your log files you should find a line "com.polarion.fme.workitemsave.actions.SaveHandler  - Loaded, script directory is 'c:\Polarion\polarion/../scripts//workitemsave/'". The displayed path may be other if you have installed polarion to another location.
	* Don't be confused about the path to the workitemsave folder. In the above example it's at "c:\Polarion\scripts\workitemsave"	

GETTING STARTED
===============

After the installation you can now easily set up your scripts. Before we go to the details, we ensure that everything works correctly:

Simply copy the "examples\title-prefix\pre-save.js" from the directory contained within this extension to your "Polarion\scripts\workitemsave\pre-save.js" folder. Now every workitem will automatically have your user id as prefix appended to the title if you save/ create a workitem.

USAGE
=====

== Script structure / order ==

You can define post and pre-save scripts in your workitemsave directory. Additionally you can restrict the scripts to the project id or the workitem type. These scripts will be only executed if the saved/created workitems are within the given project (for example 'library' and 'requirement'). To make this work, the files need to have the following syntax for pre-save-scripts:

    projectId-workitemType-pre-save.js
    workitemType-pre-save.js
    projectId-pre-save.js
    pre-save.js

Similar to post-save-scripts:

    projectId-workitemType-post-save.js
    workitemType-post-save.js
    projectId-post-save.js
    post-save.js

So if you want a pre-save script, which only belongs to workitems with type "requirement" and project "library", you have to create a script "library-requirement-pre-save.js". The extension processed the list above from specific to unspecific. That means it first looks for a script, which matches project ID and workitemtype. If that wasn't found, it looks for a workitemtype specific script and then for a project specific script. If no such script was found, the final "pre-save.js" or "post-save.js" will be executed (if exist). Therefore it's important to know, that the extension will only execute max. one pre-save and one post-save script. That means if library-requirement-pre-save.js exist and was executed, no other scripts (like the general pre-save.js) will be executed. 

If you specify both a pre- and post-save script (for example "library-requirement-pre-save.js" and "library-requirement-post-save.js" or "pre-save.js" and "post-save.js") the pre-save script is executed first if a workitem should be saved. Only if the pre-save script allows the saving, the workitem is saved and the post-script is executed.

For this feature to work, your workitem types and project types must not contains the '-' character, otherwise you can create a general "pre-save.js" and check if workitem has the given type/ project via an if statement and include your own script (The Include chapter). For assistance and debugging you can check your polarion log. Each time you save/ create a workitem, the workitemsave extension writes a detailed log message.

== Programming and Java/ Polarion bindings ==

All scripts must be written in JavaScript. You have the full functionality of JavaScript in your scripts and have access to the entire Java and Polarion API (see the SDK Javadoc()). If you want to access Classes from Java you have to provide the full name space- that means if you want to instantiate a new java calendar object, you write "var calendar = new java.util.GregorianCalendar()". Or if you want a Polarion Text object from html text, you have to write "var text = com.polarion.alm.ws.client.types.Text.html("<b>htmltext</b>")". See the examples for more information.

== Available objects within scripts ==

Within your scripts you have an easy access to some objects you may need for your custom save() action:

* 'workItem' : The workitem which should be saved (pre-save) or was saved (post-save)
* 'trackerService' : The Polarion TrackerService
* 'polarionLog' : An apache log object which makes it possible to write to the polarion log file (for debugging/ logging e.g.). You use it with polarionLog.info("info") or polarionLog.error("error")

Be careful with the names- they are case sensitive and contain upper case letters.

== Return Code/ Deny workitem save ==

If you define a pre-save script you have the possibility to deny saving the workitem. For this you have to return a value at the end of the script. The extension reads this value from your script. If you return nothing, null or an empty string the workitem can be saved. If you return any string/ value at the end of the script, saving of the workitem will be denied and the string/value will be displayed as error message to the user.

It's important that you return this value at the very last line of your script- even after functions. A good idea is to set a variable at the begin of your script like "var returnvalue=''" and set the variable to a message within your script. See the examples for details.

Be careful if you call a function with a return value at the end of your script. If your last line is "list.add(object)" and list is a java Arraylist, saving will always be denied, because the add() method returns true or false, which is interpreted as the script return value.

== Include other scripts (Experimental)

You can also include other scripts. For example if you have a specific script like "library-requirement-pre-save.js" and you also want to call the "pre-save" script, then you can include this script by:

	#include pre-save.js
	
Instead of pre-save.js you can specify any script. The script to include must be within the workitemsave folder and you must not finish the line by a semicolon or you get an error that "pre-save.js;" is not available. The scripts are "inlined", that means the extension copies the content of the other script at the position of your current script. The #include command also works recursive- that means if the included script includes other scripts, these are also included.

FAQ
===

== Can i use another language then JavaScript?

There is an experimental and untested interpreter for groovy scripts integrated. You have to use the file extension ".gy" for groovy scripts, the extension will automatically choose them.

== How can i get the "old" state of a workitem within a pre-save script?

To compare the current version of the workitem which should be saved (workItem) with the last saved one, have a look at the "field-lock" example.

== Comparing strings with each other

Sometimes a check like "if (workItem.getTitle() == "test title")" won't work (always false). This is because "test title" is a JavaScript string and workItem.getTitle() is a java string. You can avoid this problem by comparing "if (String(workItem.getTitle()) == "test title")" - where String() will wrap the java string to a JavaScript string.

== How can i check my scripts for errors?

If your script contains any syntax errors, saving the workitem will be aborted (if pre-save) and you see an error message with the line number where the error occurred. If your scripts don't run, check the polarion log and search for "workitemsave"- each time you save a workitem, you will get a detailed log message from workitemsave.

UNINSTALLATION
==============

You have to remove the extension jar file from your plugins folder. Your script won't run anymore but nothing else is affected by uninstalling this plugin.

CHANGELOG
=========

-- Version 1.3 --
* Bug: Fixed an error if saving a workitem without project and type

-- Version 1.2 --
* Bug: Fixed a bug where the plugin was inactive after a period of time (thread was terminated by polarion)
* Feature: Include other JavaScript files

-- Version 1.1 --
* Bug: Fixed stack overflow if the save() method is called for a workitem

ABOUT AND CREDITS
=================

This script comes with absolutely no warranty and support. Developed by Dirk König (Fresenius Medical Care GmbH), Dirk.Koenig@fmc-ag.com. Special Thanks to my colleague Martin Schamberger.
