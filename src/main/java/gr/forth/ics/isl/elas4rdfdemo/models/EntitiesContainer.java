package gr.forth.ics.isl.elas4rdfdemo.models;

import java.util.ArrayList;

public class EntitiesContainer {
    private int maxSize;
    private ArrayList<ResultEntity> entities;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public ArrayList<ResultEntity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<ResultEntity> entities) {
        this.entities = entities;
    }
}
