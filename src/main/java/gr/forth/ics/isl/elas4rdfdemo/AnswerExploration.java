package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;

import static org.apache.jena.riot.RDFFormat.*;

public class AnswerExploration {

    private Model model;

    public AnswerExploration(ArrayList<ResultTriple> allTriples, int size){
        if(allTriples != null ){

            if(size > allTriples.size()){
                size = allTriples.size();
            }
            // create an empty Model
            model = ModelFactory.createDefaultModel();
            for(ResultTriple triple : allTriples.subList(0,size)){
                Property pp = new PropertyImpl(triple.getPredicate());
                model.createResource(triple.getSubject()).addProperty(pp,triple.getObject());
            }
        }
    }

    /**
     * Creates the json data used by InfoVis on the graph tab
     * @return
     */
    public String createModelFromTriples(){

        if(model == null) return null;

        JSONArray jsonGraph = new JSONArray();

        HashSet<String> nodeSet = new HashSet<>();
        ResIterator resi = model.listSubjects();
        while (resi.hasNext()){
            Resource res = resi.next();
            StmtIterator stmti = res.listProperties();

            JSONObject nodeObject = new JSONObject();
            JSONArray adjacencies = new JSONArray();
            while (stmti.hasNext()){
                Statement stmt = stmti.next();
                JSONObject adjObject = new JSONObject();
                adjObject.put("nodeTo",cleanUriOrLiteral(stmt.getObject().toString()));
                adjObject.put("data", new JSONObject("{\"labeltext\":\""+cleanUriOrLiteral(stmt.getPredicate().toString())+"\"}"));
                adjacencies.put(adjObject);
            }

            String fullName = cleanUriOrLiteral(res.getURI());
            String shortName = "";
            if(fullName.length() >= 12){
                shortName = fullName.substring(0,9)+"...";
            } else {
                shortName = fullName;
            }

            nodeObject.put("adjacencies",adjacencies);

            JSONObject nodeData = new JSONObject();
            nodeData.put("$type","circle");
            nodeData.put("$dim",10);
            nodeData.put("$label-color","#05419b");
            nodeData.put("link",res.getURI());
            nodeData.put("isResource",true);
            nodeData.put("fullName",fullName);

            nodeObject.put("data",nodeData);
            nodeObject.put("name",shortName);
            nodeObject.put("id",cleanUriOrLiteral(res.getURI()));
            nodeSet.add(cleanUriOrLiteral(res.getURI()));

            jsonGraph.put(nodeObject);
        }

        resi = model.listSubjects();
        while (resi.hasNext()) {
            Resource res = resi.next();

            StmtIterator stmti = res.listProperties();

            //for object nodes that are not resources (literals)
            while (stmti.hasNext()){
                Statement stmt = stmti.next();
                if(!nodeSet.contains(cleanUriOrLiteral(stmt.getObject().toString()))){

                    String fullName = cleanUriOrLiteral(stmt.getObject().toString());
                    String shortName = "";
                    if(fullName.length() >= 12){
                        shortName = fullName.substring(0,9)+"...";
                    } else {
                        shortName = fullName;
                    }

                    JSONObject nodeObject = new JSONObject();
                    JSONObject nodeData = new JSONObject();
                    nodeData.put("$type","circle");
                    nodeData.put("$dim",10);
                    nodeData.put("fullName",fullName);
                    if(stmt.getObject().toString().startsWith("http")){
                        nodeData.put("$label-color","#05419b");
                        nodeData.put("link",stmt.getObject().toString());
                        nodeData.put("isResource",true);
                    } else{
                        nodeData.put("$label-color","#385723");
                        nodeData.put("isResource",false);
                        nodeData.put("link","none");
                    }

                    nodeObject.put("data",nodeData);
                    nodeObject.put("name",shortName);
                    nodeObject.put("id",cleanUriOrLiteral(stmt.getObject().toString()));
                    nodeSet.add(cleanUriOrLiteral(stmt.getObject().toString()));
                    jsonGraph.put(nodeObject);
                }
            }
        }

        return jsonGraph.toString();
    }

    public String createFile(String type){
        RDFFormat format = TURTLE_PRETTY;
        if(type.equals("ntriples")){
            format = NTRIPLES;
        } else if(type.equals("jsonld")){
            format = JSONLD;
        }
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, format);
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
            clean = uri.substring(0,clean.indexOf("@"));
        }else{
            clean = uri;
        }
        clean = clean.replaceAll("\\\\","");
        clean = clean.replaceAll("\"","");
        return clean.trim();
    }

}
