# Elas4RDF Demo

### Installation:

    mvn clean install -DargLine="-Xmx4g" //use at least 4g ram
    java -jar -Xmx4g target/elas4rdf-demo-0.0.1-SNAPSHOT.jar
   OR

    mvnw spring-boot:run //windows
    ./mvnw spring-boot:run //linux

Runs on:
localhost:8081/elas4rdf

### Main class for each tab

* [Triples & Entities](elas4rdfdemo/src/main/java/gr/forth/ics/isl/elas4rdfdemo/KeywordSearch.java)
* [Graph](elas4rdfdemo/src/main/java/gr/forth/ics/isl/elas4rdfdemo/AnswerExploration.java) 
* [QA](elas4rdfdemo/src/main/java/gr/forth/ics/isl/elas4rdfdemo/qa/)
* [Schema](elas4rdfdemo/src/main/java/gr/forth/ics/isl/elas4rdfdemo/SchemaTab.java)