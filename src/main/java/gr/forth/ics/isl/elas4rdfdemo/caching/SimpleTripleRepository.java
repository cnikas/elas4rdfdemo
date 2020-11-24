package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.KeywordSearch;
import gr.forth.ics.isl.elas4rdfdemo.models.TriplesContainer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class SimpleTripleRepository implements TripleRepository {

    @Override
    @Cacheable(value = "triples", key = "#query")
    public TriplesContainer searchTriples(String query) {
        KeywordSearch ks = new KeywordSearch();
        return ks.searchTriples(query);
    }
}
