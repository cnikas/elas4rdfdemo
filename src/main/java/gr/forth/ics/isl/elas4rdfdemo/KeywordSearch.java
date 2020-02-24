package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class KeywordSearch {
    public Elas4RDFRest elas4RDFRest;

    public KeywordSearch(){
        elas4RDFRest = new Elas4RDFRest();
    }

    public ArrayList<ResultTriple> searchTriples(String query){
        ArrayList<ResultTriple> triples = new ArrayList<>();
        JSONObject jo = elas4RDFRest.simpleSearch(query,1000,"eindex","triples");
        JSONArray ja = jo.getJSONObject("results").getJSONArray("triples");
        for(int i=0; i < ja.length(); i++){
            JSONObject object = ja.getJSONObject(i);
            triples.add(new ResultTriple(object.getString("sub"),object.getString("pre"),object.getString("obj"),object.getString("sub_ext"),object.getString("obj_ext")));
        }
        return triples;
    }

    public ArrayList<ResultEntity> searchEntities(String query){
        ArrayList<ResultEntity> entities = new ArrayList<>();
        JSONObject jo = elas4RDFRest.simpleSearch(query,1000,"eindex","entities");
        JSONArray ja = jo.getJSONObject("results").getJSONArray("entities");
        for(int i=0; i < ja.length(); i++){
            JSONObject object = ja.getJSONObject(i);
            entities.add(new ResultEntity(object.getString("ext"),object.getString("entity")));
        }
        return entities;
    }
}
