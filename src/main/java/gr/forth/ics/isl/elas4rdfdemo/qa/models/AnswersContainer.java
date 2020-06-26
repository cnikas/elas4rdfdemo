package gr.forth.ics.isl.elas4rdfdemo.qa.models;

import java.util.ArrayList;

/*
* Container class for all answers returned by a query
 */
public class AnswersContainer {
    private Answer topAnswer;
    private ArrayList<Answer> answers;

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
}