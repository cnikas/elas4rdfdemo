# Elas4RDF Demo

### Installation

    mvn clean install -DargLine="-Xmx4g" //use at least 4g ram
    java -jar -Xmx4g target/elas4rdf-demo-0.0.1-SNAPSHOT.jar
   OR
   
    mvnw spring-boot:run //windows
    ./mvnw spring-boot:run //linux

Runs on:
localhost:8081/elas4rdf

### Configuration (application.properties file)
* elas4rdfurl: the url of the instance of elas4rdf used
* datasetId: the dataset id on elas4rdf (e.g. dbpedia)
* extField: the field used for the extended information (e.g. rdfs_comment)

### Main class for each tab

* [Triples & Entities](src/main/java/gr/forth/ics/isl/elas4rdfdemo/KeywordSearch.java)
* [Graph](src/main/java/gr/forth/ics/isl/elas4rdfdemo/AnswerExploration.java) 
* [QA](src/main/java/gr/forth/ics/isl/elas4rdfdemo/qa/)
* [Schema](src/main/java/gr/forth/ics/isl/elas4rdfdemo/SchemaTab.java)