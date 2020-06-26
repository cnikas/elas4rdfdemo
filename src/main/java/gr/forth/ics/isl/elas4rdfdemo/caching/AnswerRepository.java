package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.AnswersContainer;

import java.util.List;

public interface AnswerRepository {

    public AnswersContainer getAnswers(String query, List<ResultEntity> entities, List<ResultTriple> triples);
}
