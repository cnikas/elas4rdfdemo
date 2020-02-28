package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;

import java.util.ArrayList;

public interface EntityRepository {

    public ArrayList<ResultEntity> searchEntities(String query);
}
