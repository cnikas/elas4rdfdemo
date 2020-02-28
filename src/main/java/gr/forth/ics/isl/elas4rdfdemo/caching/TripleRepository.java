package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;

import java.util.ArrayList;

public interface TripleRepository {
    public ArrayList<ResultTriple> searchTriples(String query);
}
