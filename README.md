# ImageJPluginTemplate
A simple template to generate ImageJ plugins for multifile processing 

  
How to use this template:

clone the repository
update the project name and metadata (Note: ImageJ requires a underscore in the name of the .jar, thus, it is recommended to choose a name containing a underscore)
	-package name
	
	-pom.xml
		-version
		-date
		-owner
		-repository URL
		-description
		-artifact ID (should be the name as everywhere else, needs to contain the underscore)
		
	-plugin.config
	
	-Run configuration
	
	-plugin Name (and version) in the Main

test the project and see if the template compiles and runs with changed settings

modify the Processing.doProcessing() method as desired
	
