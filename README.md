currency-aggregator
========

currency-aggregator provides a possibility to aggregate currency rates from files or databases.
With help of currency-aggregator you can retrieve and manage currency rates in a handy way.


Features
--------

- supports storing rates in the most common file extensions: xml, json, csv;
- supports storing rates in database;
- easy to use and intuitive API;
- provides possibility to generate report with best prices among banks;

service support storage rates in files or in database, you can configure needed storage in src\main\resources\application.properties file)
```properties
spring.profiles.active = #supported 'file' and 'db' profiles
```

Running standalone app
------------

You can check how application works by running:
```
mvn install 
mvn tomcat7:run
```    
That's all! 
Go to http://localhost:8080 

Deployment
------------

Requirements:
- tomcat 9
- maven 3

Steps to deploy:

1) In $CATALINA_HOME\conf\tomcat-users add following:
```xml
<tomcat-users>
    <role rolename="manager-gui"/>
    <role rolename="manager-script"/>
    <user username="admin" password="password" roles="manager-gui, manager-script"/>
</tomcat-users>
```
2) In $MAVEN_HOME\conf\settings.xml add following (you can change it later):
```xml
<server>
    <id>TomcatServer</id>
    <username>admin</username>
    <password>password</password>
</server>
```
3) Launch tomcat ($CATALINA_HOME\bin\startup)
4) Deploy:
```
mvn install 
mvn tomcat7:deploy
```
5) currency-aggregator is on http://localhost:8080 now.
You can change url, path and server in pom.xml in properties section, by default it's:
```xml
<properties>
    <tomcat.server>TomcatServer</tomcat.server>
    <tomcat.url>http://localhost:8080/manager/text</tomcat.url>
    <tomcat.path>/</tomcat.path>
</properties>
```
