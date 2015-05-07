 #!/bin/bash          
mvn -DskipTests=true clean install
cd parking-it
mvn embedded-glassfish:run
cd ..
