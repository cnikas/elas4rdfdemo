package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.models.Answer;

import java.util.ArrayList;

public interface AnswerRepository {

    public ArrayList<Answer> getAnswers(String query);
}
