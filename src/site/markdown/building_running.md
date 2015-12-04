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