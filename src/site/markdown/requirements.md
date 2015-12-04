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
