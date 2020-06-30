package gr.forth.ics.isl.elas4rdfdemo.qa.models;

import java.util.ArrayList;
import java.util.List;

/*
* Container class for all answers returned by a query
 */
public class AnswersContainer {
    private AnswerForUi topAnswer;
    private List<AnswerForUi> answers;
    private boolean error;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public AnswerForUi getTopAnswer() {
        return topAnswer;
    }

    public void setTopAnswer(AnswerForUi topAnswer) {
        this.topAnswer = topAnswer;
    }

    public List<AnswerForUi> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerForUi> answers) {
        this.answers = answers;
    }
}