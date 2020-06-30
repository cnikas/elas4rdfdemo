package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.qa.models.LabeledUri;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;

public class SparqlMethods {

    public static ArrayList<String[]> executeSPARQLQuery(String queryString) {
        ArrayList<String[]> answers = new ArrayList<>();
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://139.91.183.46:8899/sparql", query, "http://dbpedia.org");
        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution qs = results.next();
            RDFNode slrn = qs.get("sl");
            RDFNode prn = qs.get("p");
            RDFNode plrn = qs.get("pl");
            RDFNode orn = qs.get("o");
            answers.add(new String[]{slrn.asLiteral().getString(), prn.toString(), plrn.asLiteral().getString(),orn.toString()});
        }
        return answers;
    }

    public static ArrayList<String[]> objectsWithType(String sURI, String tURI){
        /*
       SELECT ?sl ?p ?pl ?o WHERE {<http://dbpedia.org/resource/Greece> ?p ?o .
        ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Person> .
        ?p <http://www.w3.org/2000/01/rdf-schema#label> ?pl.
        <http://dbpedia.org/resource/Greece> <http://www.w3.org/2000/01/rdf-schema#label> ?sl.}
         */
        String queryString = " SELECT ?sl ?p ?pl ?o WHERE {<"+sURI+"> ?p ?o . ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+tURI+"> . ?p <http://www.w3.org/2000/01/rdf-schema#label> ?pl . <"+sURI+"> <http://www.w3.org/2000/01/rdf-schema#label> ?sl.}";
        return executeSPARQLQuery(queryString);
    }

    public static ArrayList<String[]> objectsInRange(String sURI, String type){
        String queryString;
        if(type.equals("literal_number")){
            queryString = "SELECT ?sl ?p ?pl ?o WHERE {{<" + sURI + "> ?p ?o .\n" +
                    "         ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#integer> . ?p <http://www.w3.org/2000/01/rdf-schema#label> ?pl . <"+sURI+"> <http://www.w3.org/2000/01/rdf-schema#label> ?sl.}\n" +
                    "        UNION {\n" +
                    "         <" + sURI + "> ?p ?o .\n" +
                    "         ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#float> . ?p <http://www.w3.org/2000/01/rdf-schema#label> ?pl . <"+sURI+"> <http://www.w3.org/2000/01/rdf-schema#label> ?sl.}\n" +
                    "        UNION {\n" +
                    "         <" + sURI + "> ?p ?o .\n" +
                    "         ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#double> . ?p <http://www.w3.org/2000/01/rdf-schema#label> ?pl . <"+sURI+"> <http://www.w3.org/2000/01/rdf-schema#label> ?sl.}}";
        } else if(type.equals("literal_date")){
            queryString = "SELECT ?sl ?p ?pl ?o WHERE {<" + sURI + "> ?p ?o .\n" +
                    " ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#date> . ?p <http://www.w3.org/2000/01/rdf-schema#label> ?pl . <"+sURI+"> <http://www.w3.org/2000/01/rdf-schema#label> ?sl.}";
        } else {
            queryString = "SELECT ?sl ?p ?pl ?o WHERE {<" + sURI + "> ?p ?o .\n" +
                    " ?p <http://www.w3.org/2000/01/rdf-schema#range> <http://www.w3.org/2001/XMLSchema#string> . ?p <http://www.w3.org/2000/01/rdf-schema#label> ?pl . <"+sURI+"> <http://www.w3.org/2000/01/rdf-schema#label> ?sl.}";
        }
        return executeSPARQLQuery(queryString);
    }

}
