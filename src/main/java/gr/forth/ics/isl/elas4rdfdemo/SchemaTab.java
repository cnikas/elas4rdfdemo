package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class SchemaTab {

    public Elas4RDFRest elas4RDF;

    private String infoVisGraph;
    private ArrayList<FrequentItem> topPredicates;
    private ArrayList<FrequentItem> topClasses;
    private int size;
    private ArrayList<ResultTriple> allTriples;
    private HashMap<String, HashSet<String>> urisWithTypes;
    private HashMap<String, Integer> urisWithFreqs;

    public String getInfoVisGraph() {
        return infoVisGraph;
    }

    public void setInfoVisGraph(String infoVisGraph) {
        this.infoVisGraph = infoVisGraph;
    }

    public ArrayList<FrequentItem> getTopPredicates() {
        return topPredicates;
    }

    public void setTopPredicates(ArrayList<FrequentItem> topPredicates) {
        this.topPredicates = topPredicates;
    }

    public ArrayList<FrequentItem> getTopClasses() {
        return topClasses;
    }

    public void setTopClasses(ArrayList<FrequentItem> topClasses) {
        this.topClasses = topClasses;
    }

    public SchemaTab(){
        elas4RDF = new Elas4RDFRest();
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
        if(ja !=null){
            for(int i=0;i<ja.length();i++){
                String type = ja.getJSONObject(i).getString("obj");
                if(type.startsWith("http://dbpedia.org"))
                    types.add(type);//obj_keywords
            }
        }


        return types;
    }

    public SchemaTab(ArrayList<ResultTriple> allTriples, int size){

        this.elas4RDF = new Elas4RDFRest();

        this.allTriples = allTriples;
        this.size = size;

        if(size>allTriples.size()){
            size = allTriples.size();
        }

        HashMap<String, Integer> urisWithFreqs = new HashMap<>();
        HashMap<String,Integer> predicatesWithFreqs = new HashMap<>();
        HashMap<String, HashSet<SchemaAdjacency>> urisWithAdjacencies = new HashMap<>();

        for(ResultTriple rt: allTriples.subList(0,size)){

            //increment count for each subject and object
            int currentCountSub = 0;
            if(urisWithFreqs.containsKey(rt.getSubject())) currentCountSub = urisWithFreqs.get(rt.getSubject());
            urisWithFreqs.put(rt.getSubject(),currentCountSub+1);

            if(rt.getObject().startsWith("http")){
                int currentCountObj = 0;
                if(urisWithFreqs.containsKey(rt.getObject())) currentCountObj = urisWithFreqs.get(rt.getObject());
                urisWithFreqs.put(rt.getObject(),currentCountObj+1);
            }

            //increment count for predicates
            int currentCountPre = 0;
            if(predicatesWithFreqs.containsKey(rt.getPredicate())) currentCountPre = predicatesWithFreqs.get(rt.getPredicate());
            predicatesWithFreqs.put(rt.getPredicate(),currentCountPre+1);

            //add adjacency between subject and object
            if(rt.getObject().startsWith("http")){
                HashSet<SchemaAdjacency> tempAdjacencies;
                if(urisWithAdjacencies.containsKey(rt.getSubject())){
                    tempAdjacencies = urisWithAdjacencies.get(rt.getSubject());
                } else {
                    tempAdjacencies = new HashSet<>();
                }
                tempAdjacencies.add(new SchemaAdjacency(rt.getPredicate(),rt.getObject()));
                urisWithAdjacencies.put(rt.getSubject(),tempAdjacencies);
            }

        }
        this.urisWithFreqs = urisWithFreqs;

        //find set of types for each uri
        HashMap<String, HashSet<String>> urisWithTypes = new HashMap<>();
        for(String uri:urisWithFreqs.keySet()){
            urisWithTypes.put(uri,findTypes(uriToString(uri)));
        }
        this.urisWithTypes = urisWithTypes;

        //find count for each type
        HashMap<String,Integer> typesWithCounts = new HashMap<>();
        for(Map.Entry<String, HashSet<String>> entry : urisWithTypes.entrySet()){
            for(String type:entry.getValue()){
                int typeCount=0;
                if(typesWithCounts.containsKey(type)) typeCount = typesWithCounts.get(type);
                typesWithCounts.put(type,typeCount+urisWithFreqs.get(entry.getKey()));
            }
        }

        //find top K classes and properties
        ArrayList<FrequentItem> allFrequentClasses = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : typesWithCounts.entrySet()){
            allFrequentClasses.add(new FrequentItem(uriToString(entry.getKey()),entry.getValue(),entry.getKey()));
        }
        //sort frequent items in descending order
        allFrequentClasses.sort(new Comparator<FrequentItem>() {
            @Override
            public int compare(FrequentItem o1, FrequentItem o2) {
                return o2.getCount() - o1.getCount();
            }
        });
        this.topClasses = allFrequentClasses;

        ArrayList<FrequentItem> allFrequentPredicates = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : predicatesWithFreqs.entrySet()){
            allFrequentPredicates.add(new FrequentItem(uriToString(entry.getKey()),entry.getValue(),entry.getKey()));
        }
        //sort frequent items in descending order
        allFrequentPredicates.sort(new Comparator<FrequentItem>() {
            @Override
            public int compare(FrequentItem o1, FrequentItem o2) {
                return o2.getCount() - o1.getCount();
            }
        });
        this.topPredicates = allFrequentPredicates;

        //create nodes for the graph
        HashMap<String,SchemaNode> nodesMap = new HashMap<>();
        for(FrequentItem fi:allFrequentClasses){//can use sublist here to control size
            nodesMap.put(fi.getUri(),new SchemaNode(fi));
        }

        //find adjacencies between types
        //for each uri
        for(Map.Entry<String, HashSet<SchemaAdjacency>> entry: urisWithAdjacencies.entrySet()){
            //for each adjacency of the uri
            for(SchemaAdjacency uriAdj:entry.getValue()){
                //for each type of the uri
                for(String type:urisWithTypes.get(entry.getKey())){
                    SchemaNode currentNode = nodesMap.get(type);
                    //add adjacency with each type of the object
                    for(String objType:urisWithTypes.get(uriAdj.getNodeTo())){
                       currentNode.addAdjacency(new SchemaAdjacency(uriAdj.getLabel(),uriToString(objType)));
                    }
                    //add updated node to nodesMap
                    nodesMap.put(type,currentNode);
                }
            }
        }

        //convert nodesMap to infoVis format
        this.infoVisGraph = createInfoVisJSON(nodesMap);
    }

    public String createInfoVisJSON(HashMap<String,SchemaNode> nodesMap){

        //create json object
        JSONArray nodesArray = new JSONArray();

        for(Map.Entry<String,SchemaNode> entry : nodesMap.entrySet()){

            JSONObject typeObject = new JSONObject();
            String nodeName = uriToString(entry.getKey());
            typeObject.put("name",nodeName);
            typeObject.put("id",nodeName);
            typeObject.put("data",new JSONObject("{  \n" +
                    "        \"$color\": \"#b1ee86\",  \n" +
                    "        \"$type\": \"circle\",  \n" +
                    "        \"$dim\": "+(entry.getValue().getCount()+3)+",  \n" +
                    "\"link\": \""+entry.getValue().getUri()+"\"\n"+
                    "      }"));

            JSONArray objectAdjacenciesArray = new JSONArray();
            for(SchemaAdjacency adjacency : entry.getValue().getAdjacencies()){
                if(!entry.getKey().equals(adjacency.getNodeTo())){
                    JSONObject adjObject = new JSONObject();
                    adjObject.put("nodeTo",adjacency.getNodeTo());
                    adjObject.put("data", new JSONObject("{\"labeltext\":\""+adjacency.getLabel()+"\"}"));
                    objectAdjacenciesArray.put(adjObject);
                }
            }

            typeObject.put("adjacencies",objectAdjacenciesArray);

            nodesArray.put(typeObject);
        }

        return nodesArray.toString();
    }
    
    public String uriToString(String u){
        return u.substring(u.lastIndexOf("/")+1);
    }

    public ArrayList<ResultTriple> triplesForClass(String type){
        ArrayList<ResultTriple> result = new ArrayList<>();
        for(ResultTriple rt: allTriples.subList(0,size)){
            if(urisWithTypes.containsKey(rt.getSubject())){
                if(urisWithTypes.get(rt.getSubject()).contains(type)){
                    result.add(rt);
                }
            }
            if(urisWithTypes.containsKey(rt.getObject())){
                if(urisWithTypes.get(rt.getObject()).contains(type)){
                    result.add(rt);
                }
            }
        }
        return result;
    }

    public ArrayList<ResultEntity> entitiesForClass(String type){
        ArrayList<ResultEntity> result = new ArrayList<>();
        HashMap<String,Integer> entitiesWithCounts = new HashMap<>(this.urisWithFreqs);

        //find ext field, use frequency as score
        for(ResultTriple rt: allTriples.subList(0,size)){
            if(entitiesWithCounts.containsKey(rt.getSubject())){
                if(urisWithTypes.get(rt.getSubject()).contains(type))
                    result.add(new ResultEntity(rt.getSubExt(),rt.getSubject(),0.0,entitiesWithCounts.remove(rt.getSubject())));
            }
            if(entitiesWithCounts.containsKey(rt.getObject())){
                if(urisWithTypes.get(rt.getObject()).contains(type))
                    result.add(new ResultEntity(rt.getObjExt(),rt.getObject(),0.0,entitiesWithCounts.remove(rt.getObject())));
            }
        }
        result.sort(new Comparator<ResultEntity>() {
            @Override
            public int compare(ResultEntity o1, ResultEntity o2) {
                return Double.compare(o2.getFrequency(),o1.getFrequency());
            }
        });
        return result;
    }

    public ArrayList<ResultTriple> triplesForPredicate(String predicate){
        ArrayList<ResultTriple> result = new ArrayList<>();
        for(ResultTriple rt: allTriples.subList(0,size)){
            if(rt.getPredicate().equals(predicate)){
                result.add(rt);
            }
        }
        return result;
    }

    public ArrayList<ResultEntity> entitiesForPredicate(String predicate){
        ArrayList<ResultEntity> result = new ArrayList<>();
        HashMap<String,Integer> entitiesWithCounts = new HashMap<>(this.urisWithFreqs);

        //find ext field, use frequency as score
        for(ResultTriple rt: allTriples.subList(0,size)){
            if (rt.getPredicate().equals(predicate)) {
                if(entitiesWithCounts.containsKey(rt.getSubject())){
                    result.add(new ResultEntity(rt.getSubExt(),rt.getSubject(),0.0,entitiesWithCounts.remove(rt.getSubject())));
                }
                if(entitiesWithCounts.containsKey(rt.getObject())){
                    result.add(new ResultEntity(rt.getObjExt(),rt.getObject(),0.0,entitiesWithCounts.remove(rt.getObject())));
                }
            }

        }
        result.sort(new Comparator<ResultEntity>() {
            @Override
            public int compare(ResultEntity o1, ResultEntity o2) {
                return Double.compare(o2.getFrequency(),o1.getFrequency());
            }
        });
        return result;
    }
}
