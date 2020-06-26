package gr.forth.ics.isl.elas4rdfdemo.caching;

import gr.forth.ics.isl.elas4rdfdemo.Main;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.qa.AnswerExtraction;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.AnswersContainer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;


@Component
@SessionScope
public class SimpleAnswerRepository implements AnswerRepository{

    @Override
    @Cacheable(value="answers", key="#query")
    public AnswersContainer getAnswers(String query, List<ResultEntity> entities, List<ResultTriple> triples) {
        String type = Main.atp.predictType(query);
        AnswerExtraction ae = new AnswerExtraction();
        return ae.extractAnswers(query,type,entities,triples);
    }
}
