package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.caching.TripleRepository;
import gr.forth.ics.isl.elas4rdfdemo.models.EntitiesContainer;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.models.TriplesContainer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class KeywordSearch {
    public Elas4RDFRest elas4RDFRest;

    public KeywordSearch(){
        elas4RDFRest = new Elas4RDFRest();
    }

    public TriplesContainer searchTriples(String query){
        TriplesContainer tc = new TriplesContainer();
        ArrayList<ResultTriple> triples = new ArrayList<>();
        JSONObject jo = elas4RDFRest.simpleSearch(query,1000,"terms_eindex","triples");
        JSONArray ja = jo.getJSONObject("results").getJSONArray("triples");
        for(int i=0; i < ja.length(); i++){
            JSONObject object = ja.getJSONObject(i);
            triples.add(new ResultTriple(object.getString("sub"),object.getString("pre"),object.getString("obj"),object.getString("sub_ext"),object.getString("obj_ext")));
        }
        tc.setTriples(triples);
        tc.setMaxSize(jo.getJSONObject("results").getInt("total_triples"));
        return tc;
    }

    public EntitiesContainer searchEntities(String query){
        EntitiesContainer ec = new EntitiesContainer();
        ArrayList<ResultEntity> entities = new ArrayList<>();
        JSONObject jo = elas4RDFRest.simpleSearch(query,1000,"terms_eindex","entities");
        JSONArray ja = jo.getJSONObject("results").getJSONArray("entities");
        for(int i=0; i < ja.length(); i++){
            JSONObject object = ja.getJSONObject(i);
            entities.add(new ResultEntity(object.getString("ext"),object.getString("entity"),object.getDouble("score")));
        }
        ec.setEntities(entities);
        ec.setMaxSize(jo.getJSONObject("results").getInt("total_entities"));
        return ec;
    }
}
