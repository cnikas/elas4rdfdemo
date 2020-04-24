package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.FrequentItem;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.models.SchemaAdjacency;
import gr.forth.ics.isl.elas4rdfdemo.models.SchemaNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class SchemaTab {

    public Elas4RDFRest elas4RDF;

    public SchemaTab(){
        elas4RDF = new Elas4RDFRest();
    }

    public Object[] createSchemaGraph(ArrayList<ResultTriple> allTriples, int size){

        HashMap<String,HashSet<String>> urisWithTypes = new HashMap<>();
        HashMap<String,FrequentItem> relationsWithCount = new HashMap<>();
        HashMap<String,SchemaNode> nodeList = new HashMap<>();
        if(size>allTriples.size()){
            size = allTriples.size();
        }
        for(ResultTriple rt: allTriples.subList(0,size)){

            //if the subject is new, find its types and add it to urisWithTypes
            if(!urisWithTypes.containsKey(rt.getSubHighlight())){
                HashSet<String> types = findTypes(rt.getSubHighlight());
                urisWithTypes.put(rt.getSubHighlight(), types);
            }

            //if the object is new, find its types and add it to urisWithTypes
            if(!urisWithTypes.containsKey(rt.getObjHighlight())) {
                HashSet<String> types = findTypes(rt.getSubHighlight());
                urisWithTypes.put(rt.getObjHighlight(), types);
            }

            //update the count for each type of current subject
            for(String type:urisWithTypes.get(rt.getSubHighlight())){
                SchemaNode sn;

                if(nodeList.containsKey(type)){
                    sn = nodeList.get(type);
                } else{
                    sn = new SchemaNode();
                    sn.setName(type.substring(type.lastIndexOf("/")+1));
                    sn.setUri(type);
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
                    sn.setName(type.substring(type.lastIndexOf("/")+1));
                    sn.setUri(type);
                }

                int typeCount = sn.getCount();
                sn.setCount(typeCount+1);

                nodeList.put(type,sn);
            }

            if(rt.getPredicate().startsWith("http://dbpedia.org/ontology/")){
                int relationCount;
                if(relationsWithCount.containsKey(rt.getPreHighlight())){
                    relationCount = relationsWithCount.get(rt.getPreHighlight()).getCount();
                } else {
                    relationCount = 0;
                }
                relationsWithCount.put(rt.getPreHighlight(),new FrequentItem(rt.getPreHighlight(),relationCount+1,rt.getPredicate()));
            }
        }

        //create json object
        JSONArray nodesArray = new JSONArray();
        TreeMap<Integer,FrequentItem> allFrequentProperties = new TreeMap<Integer,FrequentItem>(Comparator.reverseOrder());
        TreeMap allFrequentClasses = new TreeMap(Comparator.reverseOrder());

        for(Map.Entry<String,FrequentItem> entry : relationsWithCount.entrySet()){
            allFrequentProperties.put(entry.getValue().getCount(),entry.getValue());
        }

        for(Map.Entry<String,SchemaNode> entry : nodeList.entrySet()){

            allFrequentClasses.put(entry.getValue().getCount(),new FrequentItem(entry.getValue().getName(),entry.getValue().getCount(),entry.getValue().getUri()));

            JSONObject typeObject = new JSONObject();
            String nodeName = entry.getKey().substring(entry.getKey().lastIndexOf("/")+1);
            typeObject.put("name",nodeName);
            typeObject.put("id",nodeName);
            typeObject.put("data",new JSONObject("{  \n" +
                        "        \"$color\": \"#b1ee86\",  \n" +
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
                                adjObject.put("nodeTo",type.substring(type.lastIndexOf("/")+1));
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

        ArrayList<FrequentItem> frequentClasses = createTopList(allFrequentClasses,5);

        for(FrequentItem fi:frequentClasses){
            JSONArray urisOfType = new JSONArray();
            String currentType = fi.getUri();
            for(Map.Entry<String,HashSet<String>> uwt:urisWithTypes.entrySet()){
                if(uwt.getValue().contains(currentType)){
                    urisOfType.put(uwt.getKey());
                }
            }

            fi.setUrisOfType(urisOfType.toString());
        }

        ArrayList frequentProperties = createTopList(allFrequentProperties,5);

        return new Object[]{nodesArray,frequentClasses,frequentProperties};
    }

    public ArrayList<FrequentItem> createTopList(TreeMap<Integer,FrequentItem> allFrequent, int limit){
        ArrayList<FrequentItem> frequent = new ArrayList<>();
        Set<Map.Entry<Integer,FrequentItem>> propertiesSet = allFrequent.entrySet();
        Iterator<Map.Entry<Integer,FrequentItem>> psi = propertiesSet.iterator();
        int i=0;
        while(psi.hasNext() && i<limit){
            frequent.add(psi.next().getValue());
            i++;
        }
        return frequent;
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
            if(ja.getJSONObject(i).getString("obj").startsWith("http://dbpedia.org/ontology/"))
                types.add(ja.getJSONObject(i).getString("obj"));//obj_keywords
            //types -> hashmap
        }

        return types;
    }
}
