package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.models.SchemaAdjacency;
import gr.forth.ics.isl.elas4rdfdemo.models.SchemaNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SchemaTab {

    public Elas4RDFRest elas4RDF;

    public SchemaTab(){
        elas4RDF = new Elas4RDFRest();
    }

    public String createSchemaGraph(ArrayList<ResultTriple> allTriples, int size){

        HashMap<String,HashSet<String>> urisWithTypes = new HashMap<>();
        HashMap<String,Integer> relationsWithCount = new HashMap<>();
        HashMap<String,SchemaNode> nodeList = new HashMap<>();
        if(size>allTriples.size()){
            size = allTriples.size();
        }
        for(ResultTriple rt: allTriples.subList(0,size)){

            //if the subject is new, find its types and add it to urisWithTypes
            if(!urisWithTypes.containsKey(rt.getSubHighlight())) {
                System.out.println("found new uri: " + rt.getSubHighlight());
                HashSet<String> types = findTypes(rt.getSubHighlight());
                System.out.println("found types: " + types.toString());
                urisWithTypes.put(rt.getSubHighlight(), types);
            }

            //if the object is new, find its types and add it to urisWithTypes
            if(!urisWithTypes.containsKey(rt.getObjHighlight())) {
                System.out.println("found new uri: " + rt.getSubHighlight());
                HashSet<String> types = findTypes(rt.getSubHighlight());
                System.out.println("found types: " + types.toString());
                urisWithTypes.put(rt.getObjHighlight(), types);
            }

            //update the count for each type of current subject
            for(String type:urisWithTypes.get(rt.getSubHighlight())){
                SchemaNode sn;

                if(nodeList.containsKey(type)){
                    sn = nodeList.get(type);
                } else{
                    sn = new SchemaNode();
                }

                int typeCount = sn.getCount();
                sn.setCount(typeCount+1);

                HashSet<SchemaAdjacency> adjacencies = sn.getAdjacencies();
                adjacencies.add(new SchemaAdjacency(rt.getPreHighlight(),rt.getObjHighlight()));
                sn.setAdjacencies(adjacencies);

                nodeList.put(type,sn);
            }

            for(String type:urisWithTypes.get(rt.getObjHighlight())){
                SchemaNode sn;

                if(nodeList.containsKey(type)){
                    sn = nodeList.get(type);
                } else{
                    sn = new SchemaNode();
                }

                int typeCount = sn.getCount();
                sn.setCount(typeCount+1);

                nodeList.put(type,sn);
            }

            int relationCount = relationsWithCount.containsKey(rt.getPreHighlight()) ? relationsWithCount.get(rt.getPreHighlight()) : 0;
            relationsWithCount.put(rt.getPreHighlight(),relationCount+1);

        }

        //create json object
        JSONArray nodesArray = new JSONArray();
        JSONArray relationsArray = new JSONArray();

        for(Map.Entry<String,SchemaNode> entry : nodeList.entrySet()){
            JSONObject typeObject = new JSONObject();
            typeObject.put("name",entry.getKey());
            typeObject.put("id",entry.getKey());
            typeObject.put("data",new JSONObject("{  \n" +
                    "        \"$color\": \"#83548B\",  \n" +
                    "        \"$type\": \"circle\",  \n" +
                    "        \"$dim\": "+entry.getValue().getCount()+"  \n" +
                    "      }"));

            JSONArray objectAdjacenciesArray = new JSONArray();
            for(SchemaAdjacency adjacency : entry.getValue().getAdjacencies()){
                if(urisWithTypes.containsKey(adjacency.getNodeTo())){
                    if(!urisWithTypes.get(adjacency.getNodeTo()).isEmpty()){
                        for(String type : urisWithTypes.get(adjacency.getNodeTo())){
                            if(!type.equals(entry.getKey())){
                                JSONObject adjObject = new JSONObject();
                                adjObject.put("nodeTo",type);
                                adjObject.put("data", new JSONObject("{\"labeltext\":\""+adjacency.getLabel()+"\"}"));
                                objectAdjacenciesArray.put(adjObject);
                            }
                        }
                    }
                }
            }

            typeObject.put("adjacencies",objectAdjacenciesArray);

            nodesArray.put(typeObject);
        }
        for(Map.Entry<String,Integer> entry : relationsWithCount.entrySet()){
            relationsArray.put(new JSONObject("{\"name\":\""+entry.getKey()+"\",\"count\":\""+entry.getValue()+"\"}"));
        }

        return nodesArray.toString();
    }

    public HashSet<String> findTypes(String resource){

        HashSet<String> types = new HashSet<>();

        JSONObject jo = elas4RDF.exactSubjectTypeRequest(resource);

        JSONObject resultsObject = null;
        if(jo != null){
            resultsObject = jo.optJSONObject("results");
        }
        JSONArray ja = null;
        if( resultsObject != null){
            ja = resultsObject.optJSONArray("triples");
        }

        for(int i=0;i<ja.length();i++){
            types.add(ja.getJSONObject(i).getString("obj_keywords"));
        }

        return types;
    }
}
