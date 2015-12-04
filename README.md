Introduction
------------

Parking is Java EE 7 application supporting parking space booking. It is useful in scenario where there are more users than available parking space. Places are assigned to users for given day depending on scheduling algorithm. It takes into account:

- user type (VIP or regular user)
- user points (preferred users with less points - points are added for every assigned day)
- booking timestamp (preferred users who earlier booked the place)

In long-term available places are shared fairly amongst all users.

Requirements
------------

Java 1.8+ is required to compile and Java EE 7 application server is needed to host the application (tested on [GlassFish](https://glassfish.java.net/)).

[Maven](https://maven.apache.org/) is the preferred tool to build the project. 

Beside that the following libraries are used:

- [Joda Time](http://www.joda.org/joda-time/) for date and time handling
- [Flyway](http://flywaydb.org/) for database migrations
- [FreeMarker](http://freemarker.incubator.apache.org/) to generate emails content
- [Dozer](http://dozer.sourceforge.net/) as bean to bean mapper
- [PrimeFaces](http://www.primefaces.org/) for web presentation and [PrettyFaces](http://www.ocpsoft.org/prettyfaces/) for URL rewriting
- [JUnit](http://junit.org/) and [Mockito](http://mockito.org/) for unit tests
- [Arquillian](http://arquillian.org/) and [GreenMail](http://www.icegreen.com/greenmail/) for integration tests

Building and running
--------------------

To package the project, run in parking root directory:

```
mvn clean package
```

It can be started in *demo mode* - [Maven Embedded GlassFish Plugin](https://embedded-glassfish.java.net/nonav/plugindocs/3.1/plugin-info.html) is used to deploy and run the application. Due to the limitations of embedded GlassFish, EJB timers are not running (parking places will not be assigned automatically). In *demo mode* GreenMail is used as SMTP server, so emails sent by the application will not reach their destinations.

To run it in *demo mode*, invoke:

```
mvn clean verify -Pdemo
```

and open:

```
http://localhost:8080/parking/
```

in the browser. The database is filled with example data (users and parking instances). You can login as:

- regular user (login: *user*, password: *user*)
- administrator (login: *admin*, password: *admin*)
 
To shutdown the application, type *X* and press *Enter* in the console were it was started.

Documentation
-------------

Project documentation is generated using [Maven Site Plugin](https://maven.apache.org/plugins/maven-site-plugin/). It contains more information about the application, including GlassFish deployment tutorial.

To generate the documentation, invoke in parking root directory:

```
mvn clean site site:stage
```

and open *index.html* file located in *./target/staging/* directory.
