angular_websockets_security
===========================

[![Build Status](https://travis-ci.org/Rob-Leggett/angular_websockets_security.svg?branch=master)](https://travis-ci.org/Rob-Leggett/angular_websockets_security)

Angular JS with Bootstrap, Web Sockets, Spring 4, and Spring Security

This example is an angular js single page application (SPA) with bootstrap for the widgets and styling.

The application has been broken into four modules RESTFUL-API, WEBSOCKET-API, SECURITY and CLIENT, all are built separately and all are deployed separately.

The RESTFUL-API and WEBSOCKET-API can run on any web server, but it has been tested against Tomcat 8, the server required http DELETE and PUT, so ensure your web server can support those http methods.

The CLIENT currently is run via gulp, for a production release you could extract the .zip artefact and run the static client via Apache.

Ensure that you proxy the RESTFUL-API and WEBSOCKET-API so that you have the same domain otherwise you will experience CORS related issues. (deployed artefacts only)

### Gulp:
Used as the build tool for the client, this has been written using ES6

### Spring 4:
Used to create RESTful controller interfaces which in turn gets called through ajax requests.

### Spring Security 4:
Used for a stateless api that allows authentication via basic authentication or token authentication.

Upon authentication a token is attached to the header response which can in turn be used for sequential requests to be authenticated against.

When an authentication fails a 401 will always be returned.

### Login Details as per database inject.sql:
**Username =** user@example.com

**Password =** password

Testing
====================
Simply run on the parent pom to have node and modules auto install and execute all tests. **(REQUIRED FOR FIRST RUN)**

Ensure you have Maven 3.2.0+

**mvn clean install**

To run specific profiles please run mvn clean install and simple pass the profile you wish to execute.

This will execute Java and Jasmine tests that will test both java classes and angular js files.

You can also run jasmine only tests if you wish via the front end:

**http://localhost:4444/test**

Running
====================

### Run the API via Tomcat 8:

Download Tomcat `8.5.11` and unzip it to your `$TOMCAT_HOME`, possibly your folder name would be `apache-tomcat-8.5.11`.
No need to change anything in the `$TOMCAT_HOME/conf` folder.

Deploy exploded restful-api and websocket-api war files to Tomcat 8 `$TOMCAT_HOME/webapps`,
and ensure the root context is set to API. 
Start and Stop tomcat by running `startup.sh & shutdown.sh` from `$TOMCAT_HOME/bin/`

The following would be useful UNIX command patter when you are you `angular_websockets_security` folder:

`$TOMCAT_HOME/bin/shutdown.sh;`

`mvn clean install;`

`rm -rf $TOMCAT_HOME/webapps/restful-api* $TOMCAT_HOME/webapps/websocket-api*;`

`cp **/**/*.war $TOMCAT_HOME/webapps;`

`$TOMCAT_HOME/bin/startup.sh;`

`cd client; gulp run;`

If you wanna redeploy the server part, terminate client side first by `Ctrl+C` then `cd ..` then restart the loop above.


### the restful api

The default is expecting the context root to be http://localhost:8080/restful-api/

### the websocket api

The default is expecting the context root to be http://localhost:8080/websocket-api/

### Run the CLIENT via gulp.babel.js:

Where PATH is the directory to your checked out project.

**Gulp File:** PATH\angular_websockets_security\client\gulpfile.babel.js

**Tasks:** run

**Node Interpreter:** PATH\angular_websockets_security\client\node\node.exe

**Gulp package:** PATH\angular_websockets_security\client\node_modules\gulp

### The application is set to run on

**http://localhost:4444**

Donations
====================

### How you can help?

Any donations received will be able to assist me provide more blog entries and examples via GitHub, any contributions provided is greatly appreciated.

Thanks for your support.

[![paypal](https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=EV2ZLZBABFJ34&lc=AU&item_name=Research%20%26%20Development&currency_code=AUD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)
