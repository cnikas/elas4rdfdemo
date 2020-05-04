package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.KeywordSearch;
import gr.forth.ics.isl.elas4rdfdemo.models.EntitiesContainer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class SimpleEntityRepository implements EntityRepository {
    @Override
    @Cacheable(value="entities",key="#query")
    public EntitiesContainer searchEntities(String query, int size) {
        KeywordSearch ks = new KeywordSearch();
        return ks.searchEntities(query, size);
    }
}
