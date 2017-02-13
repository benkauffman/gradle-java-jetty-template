#KrashidBuilt - Template

KrashidBuilt Java Jetty Gradle Microservice
=========================================

This is a simple REST api to interact with user data through CRUD

Running the webservice
---------------------- 
If you're going to launch the application via the commandline...  
Wrapper scripts used to start up the proper environment, can be found in the "run" directory.  
Basically `-Penvironment="dev"` is trailing the gradle command, "dev" is the environment in this example.


Gradle Build
-----
`gradle ` followed by the gradle command    

`clean build` will package the source into */build/libs/* which can be deployed through a web server  
`jettyRun` will spin up an integrated jetty web server that you can be hit locally at http://localhost:8080  
`gradle clean test integrationTest` will perform integration and unit tests 
 

Microservice information
------------------------
The base url endpoint is http://localhost:8080/api/  
The secured resource is http://localhost:8080/api/private/  
The open resource is http://localhost:8080/api/public/  
[Swagger](http://swagger.io/) documentation is available at http://localhost:8080/api-doc   

Settings & Configurations
-------------------------
I've included [Netflix Archaius](https://github.com/Netflix/archaius/wiki/Users-Guide) to manage environment settings & configurations  
