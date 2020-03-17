package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.qa.AnswerExtraction;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.AnswersContainer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;


@Component
@SessionScope
public class SimpleAnswerRepository implements AnswerRepository{

    @Override
    @Cacheable("answers")
    public AnswersContainer getAnswers(String query) {
        AnswerExtraction ae = new AnswerExtraction();
        return ae.extractAnswers(query);
    }
}
