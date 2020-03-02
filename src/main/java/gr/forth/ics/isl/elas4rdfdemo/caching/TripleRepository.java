package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.models.TriplesContainer;

public interface TripleRepository {
    public TriplesContainer searchTriples(String query);
}
