package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.qa.models.AnswersContainer;

public interface AnswerRepository {

    public AnswersContainer getAnswers(String query);
}
