package gr.forth.ics.isl.elas4rdfdemo;

import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import gr.forth.ics.isl.elas4rdfdemo.models.FrequentItem;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.models.SchemaAdjacency;
import gr.forth.ics.isl.elas4rdfdemo.models.SchemaNode;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.security.provider.certpath.AdjacencyList;

import java.util.*;

public class SchemaTab {

    public Elas4RDFRest elas4RDF;

    public SchemaTab(){
        elas4RDF = new Elas4RDFRest();
    }

    public ArrayList<FrequentItem> createTopList(ArrayList<FrequentItem> allFrequent, int k){
        //sort frequent items in descending order
        allFrequent.sort(new Comparator<FrequentItem>() {
            @Override
            public int compare(FrequentItem o1, FrequentItem o2) {
                return o2.getCount() - o1.getCount();
            }
        });

        //add the top k elements in a list
        ArrayList<FrequentItem> frequent = new ArrayList<>();
        Iterator<FrequentItem> fi = allFrequent.iterator();
        int i=0;
        while(fi.hasNext() && i<k){
            frequent.add(fi.next());
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
            String type = ja.getJSONObject(i).getString("obj");
            if(type.startsWith("http://www.w3.org") || type.startsWith("http://dbpedia.org") || type.startsWith("http://schema.org"))
                types.add(type);//obj_keywords
            //types -> hashmap
        }

        return types;
    }

    public Object[] createSchemaGraph(ArrayList<ResultTriple> allTriples, int size){

        if(size>allTriples.size()){
            size = allTriples.size();
        }

        HashMap<String,Integer> urisWithFreqs = new HashMap<>();
        HashMap<String,Integer> predicatesWithFreqs = new HashMap<>();
        HashMap<String,HashSet<SchemaAdjacency>> urisWithAdjacencies = new HashMap<>();

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

        //find set of types for each uri
        HashMap<String,HashSet<String>> urisWithTypes = new HashMap<>();
        for(String uri:urisWithFreqs.keySet()){
            urisWithTypes.put(uri,findTypes(uriToString(uri)));
        }

        //find count for each type
        HashMap<String,Integer> typesWithCounts = new HashMap<>();
        for(Map.Entry<String,HashSet<String>> entry : urisWithTypes.entrySet()){
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
        ArrayList<FrequentItem> topClasses = createTopList(allFrequentClasses,5);

        ArrayList<FrequentItem> allFrequentPredicates = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : predicatesWithFreqs.entrySet()){
            allFrequentPredicates.add(new FrequentItem(uriToString(entry.getKey()),entry.getValue(),entry.getKey()));
        }
        ArrayList<FrequentItem> topPredicates = createTopList(allFrequentPredicates,5);

        //create nodes for the graph
        HashMap<String,SchemaNode> nodesMap = new HashMap<>();
        for(FrequentItem fi:allFrequentClasses){//can use sublist here to control size
            nodesMap.put(fi.getUri(),new SchemaNode(fi));
        }

        //find adjacencies between types
        //for each uri
        for(Map.Entry<String,HashSet<SchemaAdjacency>> entry: urisWithAdjacencies.entrySet()){
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
        String infoVisGraph = createInfoVisJSON(nodesMap);

        return new Object[]{infoVisGraph,topClasses,topPredicates};

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
                    "        \"$dim\": "+entry.getValue().getCount()+"  \n" +
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
}
