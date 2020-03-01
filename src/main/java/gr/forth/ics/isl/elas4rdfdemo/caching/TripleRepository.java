package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.models.TriplesContainer;

import java.util.ArrayList;

public interface TripleRepository {
    public TriplesContainer searchTriples(String query);
}
