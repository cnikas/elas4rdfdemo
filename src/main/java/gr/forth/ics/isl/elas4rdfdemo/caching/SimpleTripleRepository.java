package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.KeywordSearch;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SimpleTripleRepository implements TripleRepository {

    @Override
    @Cacheable("triples")
    public ArrayList<ResultTriple> searchTriples(String query) {
        KeywordSearch ks = new KeywordSearch();
        return ks.searchTriples(query);
    }
}
