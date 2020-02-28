package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.KeywordSearch;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SimpleEntityRepository implements EntityRepository {
    @Override
    @Cacheable("entities")
    public ArrayList<ResultEntity> searchEntities(String query) {
        KeywordSearch ks = new KeywordSearch();
        return ks.searchEntities(query);
    }
}
