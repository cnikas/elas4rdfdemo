package gr.forth.ics.isl.elas4rdfdemo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.json.JSONObject;

import java.util.ArrayList;

import static org.apache.jena.riot.RDFFormat.TURTLE_PRETTY;

public class AnswerExploration {

    public static void createModel(ArrayList<JSONObject> allTriples){

        if(allTriples == null){
            return;
        }

        // create an empty Model
        Model model = ModelFactory.createDefaultModel();
        for(JSONObject triple : allTriples){
            Property pp = new PropertyImpl(triple.getString("pre"));
            model.createResource(triple.getString("sub")).addProperty(pp,triple.getString("obj"));
        }

        RDFDataMgr.write(System.out, model, TURTLE_PRETTY);
    }
}
