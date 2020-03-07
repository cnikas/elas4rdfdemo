package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.caching.TripleRepository;
import gr.forth.ics.isl.elas4rdfdemo.models.EntitiesContainer;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.models.TriplesContainer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class KeywordSearch {
    public Elas4RDFRest elas4RDFRest;

    public KeywordSearch(){
        elas4RDFRest = new Elas4RDFRest();
    }

    public TriplesContainer searchTriples(String query){
        TriplesContainer tc = new TriplesContainer();
        ArrayList<ResultTriple> triples = new ArrayList<>();
        int maxSize = 0;
        HashSet<String> uniqueTriples = new HashSet<>();
        JSONObject jo = elas4RDFRest.simpleSearch(query,1000,"terms_eindex","triples");

        JSONObject resultsObject = null;
        if(jo != null){
            resultsObject = jo.optJSONObject("results");
        }
        JSONArray ja = null;
        if( resultsObject != null){
            ja = resultsObject.optJSONArray("triples");
            maxSize = resultsObject.optInt("total_triples");
        }

        if(ja != null){
            for(int i=0; i < ja.length(); i++){
                JSONObject object = ja.getJSONObject(i);
                //to remove duplicate triples with same subject after removal of -,_ characters and same predicate.
                String uniqueConcatString = object.getString("sub").replaceAll("[-_]","")+object.getString("pre");
                if(!uniqueTriples.contains(uniqueConcatString)){
                    triples.add(new ResultTriple(object.getString("sub"),object.getString("pre"),object.getString("obj"),object.getString("sub_ext"),object.getString("obj_ext")));
                    uniqueTriples.add(uniqueConcatString);
                }
            }
        }

        tc.setTriples(triples);
        tc.setMaxSize(maxSize);
        return tc;
    }

    public EntitiesContainer searchEntities(String query, int size){
        EntitiesContainer ec = new EntitiesContainer();
        ArrayList<ResultEntity> entities = new ArrayList<>();
        int maxSize = 0;
        JSONObject jo = elas4RDFRest.simpleSearch(query,size,"terms_eindex","entities");

        JSONObject resultsObject = null;
        if(jo != null){
            resultsObject = jo.optJSONObject("results");
        }
        JSONArray ja = null;
        if( resultsObject != null){
            ja = resultsObject.optJSONArray("entities");
            maxSize = resultsObject.optInt("total_entities");
        }

        if(ja != null){
            for(int i=0; i < ja.length(); i++){
                JSONObject object = ja.getJSONObject(i);
                entities.add(new ResultEntity(object.getString("ext"),object.getString("entity"),object.getDouble("score")));
            }
        }

        ec.setEntities(entities);
        ec.setMaxSize(maxSize);
        return ec;
    }
}
