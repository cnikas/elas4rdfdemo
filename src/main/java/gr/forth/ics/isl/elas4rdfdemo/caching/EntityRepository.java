package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.models.EntitiesContainer;

public interface EntityRepository {

    public EntitiesContainer searchEntities(String query);
}
