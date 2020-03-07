package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.KeywordSearch;
import gr.forth.ics.isl.elas4rdfdemo.models.EntitiesContainer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class SimpleEntityRepository implements EntityRepository {
    @Override
    @Cacheable("entities")
    public EntitiesContainer searchEntities(String query) {
        KeywordSearch ks = new KeywordSearch();
        return ks.searchEntities(query);
    }
}
