package gr.forth.ics.isl.elas4rdfdemo.qa;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.Answer;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.AnswersContainer;
import java.util.*;

/**
 * This class contains methods for answer extraction.
 */

public class AnswerExtraction {

    public AnswersContainer extractAnswers(String question, String type, List<ResultEntity> entities, List<ResultTriple> triples){

        AnswersContainer ac = new AnswersContainer();
        ArrayList<Answer> answers = new ArrayList<>();
        if(type.equals("boolean")){
            answers.add(answerBoolean(question,triples));
        } else if (type.startsWith("literal")){
            answers.addAll(answerLiteral(type,triples));
            answers.addAll(answerLiteralWithTopEntities(type,entities.subList(0,3)));
        } else {
            answers.addAll(answerResource(type, entities));
            answers.addAll(answerResourceWithTopEntities(type,entities.subList(0,3)));
        }

        System.out.println("answers: "+answers);
        ac.setTopAnswer(answers.remove(0));
        ac.setAnswers(answers);
        return ac;
    }

    public ArrayList<Answer> answerResourceWithTopEntities(String type, List<ResultEntity> entities){
        ArrayList<Answer> answers = new ArrayList<>();
        for(ResultEntity entity:entities){
            ArrayList<String> answersForEntity = SparqlMethods.objectsWithType(entity.getEntity(),type);
            for(String afe:answersForEntity){
                answers.add(new Answer(afe,type,"entity",entity.getEntity()));
            }
        }
        return answers;
    }

    public ArrayList<Answer> answerLiteralWithTopEntities(String type, List<ResultEntity> entities){
        ArrayList<Answer> answers = new ArrayList<>();
        for(ResultEntity entity:entities){
            ArrayList<String> answersForEntity = SparqlMethods.objectsInRange(entity.getEntity(),type);
            for(String afe:answersForEntity){
                answers.add(new Answer(afe,type,"entity",entity.getEntity()));
            }
        }
        return answers;
    }

    public ArrayList<Answer> answerResource(String type, List<ResultEntity> entities){
        ArrayList<Answer> answerEntities = new ArrayList<>();
        for(ResultEntity entity:entities){
            List<String> foundEntityTypes = SparqlMethods.typesQuery(entity.getEntity());
            for(String foundEntitytype:foundEntityTypes){
                if(type.equals(foundEntitytype)){
                    answerEntities.add(new Answer(entity.getUriLabel(),type,"entity",entity.getEntity()));
                }
            }
        }
        return answerEntities;
    }

    public ArrayList<Answer> answerLiteral(String type, List<ResultTriple> triples){
        List<String> ranges;
        if(type.equals("literal_string")){
            ranges = Arrays.asList("http://www.w3.org/2001/XMLSchema#string");
        } else if (type.equals("literal_number")){
            ranges = Arrays.asList("http://www.w3.org/2001/XMLSchema#integer","http://www.w3.org/2001/XMLSchema#float","http://www.w3.org/2001/XMLSchema#double");
        } else {
            ranges = Arrays.asList("http://www.w3.org/2001/XMLSchema#date");
        }

        ArrayList<Answer> answerTriples = new ArrayList<>();
        for(ResultTriple triple:triples){
            List<String> literalRanges = SparqlMethods.literalRangeQuery(triple.getPredicate());
            for(String range:literalRanges){
                if(ranges.contains(type)){
                    answerTriples.add(new Answer(triple.uriToString(triple.getObject()),range,"property",triple.getPredicate()));
                }
            }
        }

        return answerTriples;
    }

    public Answer answerBoolean(String question, List<ResultTriple> triples){
        boolean ans = false;

        String[] tokens = question.toLowerCase().split(" ");
        ResultTriple rightTriple = null;
        for (ResultTriple triple : triples) {
            int componentsWithToken = 0;
            for (String token : tokens) {
                if (triple.getSubject().toLowerCase().contains(token)) {
                    componentsWithToken++;
                    break;
                }
            }
            for (String token : tokens) {
                if (triple.getPredicate().toLowerCase().contains(token)) {
                    componentsWithToken++;
                    break;
                }
            }
            for (String token : tokens) {
                if (triple.getObject().toLowerCase().contains(token)) {
                    componentsWithToken++;
                    break;
                }
            }

            if (componentsWithToken >= 2) {
                ans = true;
                rightTriple = triple;
                break;
            }
        }
        if(ans){
            return new Answer("Yes","boolean",rightTriple.toString(),"");
        } else{
            return new Answer("No","boolean","","");
        }
    }

}