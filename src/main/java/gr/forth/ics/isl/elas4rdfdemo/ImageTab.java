package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.ImageTabEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;

import java.util.*;
import java.util.stream.Collectors;

public class ImageTab {

    public static void createImageTab(ArrayList<ResultEntity> entities, int size){

        HashSet<String> resultEntityUris = new HashSet<String>(entities.subList(0,size).stream().map(ResultEntity::getUri).collect(Collectors.toList()));
        HashMap<String,ImageTabEntity> allEntities = new HashMap<>();

        //get adjacencies of each result entity (extra)
        for(String reu : resultEntityUris){
            ImageTabEntity ite = new ImageTabEntity();
            ite.setUri(reu);
            List<String> adjacencies = SparqlMethods.adjacentEntities(reu);
            ite.setAdjacencies(adjacencies);
            allEntities.put(reu,ite);
            for(String adj : adjacencies){
                if(!allEntities.containsKey(adj)){
                    ImageTabEntity iteAdj = new ImageTabEntity();
                    iteAdj.setUri(adj);
                    allEntities.put(adj,iteAdj);
                }
            }
        }

        //find images for all entities
        for(Map.Entry<String,ImageTabEntity> entry : allEntities.entrySet()){
            entry.getValue().setImageUrl(SparqlMethods.thumbnail(entry.getValue().getUri()));
        }

        for(ImageTabEntity ite : allEntities.values()){
            System.out.println(ite.getUri());
            System.out.println(ite.getImageUrl());
            for(String adj : ite.getAdjacencies()){
                System.out.println("\t"+adj);
            }
            System.out.println("\n");
        }
    }
}
