package gr.forth.ics.isl.elas4rdfdemo.qa;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;

public class SparqlMethods {

    public static ArrayList<String> executeSPARQLQuery(String queryString) {
        ArrayList<String> answers = new ArrayList<>();
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://139.91.183.46:8899/sparql", query, "http://dbpedia.org");
        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            RDFNode rn = results.next().get("o");
            answers.add(rn.toString());
        }
        return answers;
    }

    public static ArrayList<String> typesQuery(String eURI) {
        //SELECT ?o WHERE {<http://dbpedia.org/resource/Aladdin> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o}
        String queryString = "SELECT ?o WHERE {<"+eURI+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o}";

        return executeSPARQLQuery(queryString);
    }

    public static ArrayList<String> objectsWithType(String sURI, String tURI){
        String queryString = "SELECT ?o WHERE {<"+sURI+"> ?p ?o . ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+tURI+">}";
        return executeSPARQLQuery(queryString);
    }

    public static ArrayList<String> objectsInRange(String sURI, String type){
        String queryString;
        if(type.equals("literal_number")){
            queryString = "SELECT ?o WHERE {{<" + sURI + "> ?p ?o .\n" +
                    "         ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#integer> .}\n" +
                    "        UNION {\n" +
                    "         <" + sURI + "> ?p ?o .\n" +
                    "         ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#float> .}\n" +
                    "        UNION {\n" +
                    "         <" + sURI + "> ?p ?o .\n" +
                    "         ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#double> .}}";
        } else if(type.equals("literal_date")){
            queryString = "SELECT ?p ?o WHERE {<" + sURI + "> ?p ?o .\n" +
                    " ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#date> .}";
        } else {
            queryString = "SELECT ?p ?o WHERE {<" + sURI + "> ?p ?o .\n" +
                    " ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#string> .}";
        }
        return executeSPARQLQuery(queryString);
    }

    public static ArrayList<String> literalRangeQuery(String pURI) {
        /*
        SELECT ?o WHERE {<http://dbpedia.org/ontology/address> <http://www.w3.org/2000/01/rdf-schema#range> ?o .}

         */
        String queryString = "SELECT ?o WHERE {<"+pURI+"> <http://www.w3.org/2000/01/rdf-schema#range> ?o .}";

        return executeSPARQLQuery(queryString);
    }
}
