package gr.forth.ics.isl.elas4rdfdemo.qa.models;

import gr.forth.ics.isl.elas4rdfdemo.qa.models.Answer;

import java.util.ArrayList;

/*
* Container class for all answers returned by a query
 */
public class AnswersContainer {
    private Answer topAnswer;
    private ArrayList<Answer> answers;
    private String type;
    private boolean isList;

    public Answer getTopAnswer() {
        return topAnswer;
    }

    public void setTopAnswer(Answer topAnswer) {
        this.topAnswer = topAnswer;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }
}