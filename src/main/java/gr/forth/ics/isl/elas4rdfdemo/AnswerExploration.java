package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.Answer;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.ArrayList;

import static org.apache.jena.riot.RDFFormat.TURTLE_PRETTY;

public class AnswerExploration {

    private Model model;

    public AnswerExploration(ArrayList<ResultTriple> allTriples, int size){
        if(allTriples != null){
            // create an empty Model
            model = ModelFactory.createDefaultModel();
            for(ResultTriple triple : allTriples.subList(0,size)){
                Property pp = new PropertyImpl(triple.getPredicate());
                model.createResource(triple.getSubject()).addProperty(pp,triple.getObject());
            }
        }
    }
    public JSONArray createModel(ArrayList<Answer> allTriples){


        if(allTriples == null){
            return null;
        }

        // create an empty Model
        Model model = ModelFactory.createDefaultModel();
        for(Answer ans : allTriples){
            Property pp = new PropertyImpl(ans.getTripleOrigin().getString("pre"));
            model.createResource(ans.getTripleOrigin().getString("sub")).addProperty(pp,ans.getTripleOrigin().getString("obj"));
        }

        JSONArray jsonGraph = new JSONArray();

        ResIterator resi = model.listSubjects();
        while (resi.hasNext()){
            Resource res = resi.next();
            StmtIterator stmti = res.listProperties();

            JSONObject nodeObject = new JSONObject();
            JSONArray adjacencies = new JSONArray();
            while (stmti.hasNext()){
                Statement stmt = stmti.next();
                adjacencies.put(cleanUriOrLiteral(stmt.getObject().toString()));
            }
            nodeObject.put("adjacencies",adjacencies);
            nodeObject.put("data",new JSONObject("{\"$color\": \"#000\",\"$type\": \"circle\",\"$dim\": 10}"));
            nodeObject.put("name",cleanUriOrLiteral(res.getURI()));
            nodeObject.put("id",cleanUriOrLiteral(res.getURI()));

            jsonGraph.put(nodeObject);
        }
        //RDFDataMgr.write(System.out, model, TURTLE_PRETTY);
        return jsonGraph;
    }

    public JSONArray createModelFromTriples(){

        if(model == null) return null;

        JSONArray jsonGraph = new JSONArray();

        ResIterator resi = model.listSubjects();
        while (resi.hasNext()){
            Resource res = resi.next();
            StmtIterator stmti = res.listProperties();

            JSONObject nodeObject = new JSONObject();
            JSONArray adjacencies = new JSONArray();
            while (stmti.hasNext()){
                Statement stmt = stmti.next();
                adjacencies.put(cleanUriOrLiteral(stmt.getObject().toString()));
            }
            nodeObject.put("adjacencies",adjacencies);
            nodeObject.put("data",new JSONObject("{\"$color\": \"#000\",\"$type\": \"circle\",\"$dim\": 10}"));
            nodeObject.put("name",cleanUriOrLiteral(res.getURI()));
            nodeObject.put("id",cleanUriOrLiteral(res.getURI()));

            jsonGraph.put(nodeObject);
        }

        return jsonGraph;
    }

    public String createFile(){
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, TURTLE_PRETTY);
        return sw.toString();
    }

    /*
     * Converts a uri or a literal to a plain string.
     */
    public String cleanUriOrLiteral(String uri){
        String clean = "";
        if(uri.startsWith("http")){
            clean = uri.substring(uri.lastIndexOf("/")+1);
        }else if(clean.contains("@")){
            clean = clean.substring(0,clean.indexOf("@"));
        }else{
            clean = uri;
        }
        return clean.trim();
    }
}
