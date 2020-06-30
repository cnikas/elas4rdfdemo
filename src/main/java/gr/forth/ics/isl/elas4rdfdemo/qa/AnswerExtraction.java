package gr.forth.ics.isl.elas4rdfdemo.qa;

import gr.forth.ics.isl.elas4rdfdemo.SparqlMethods;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.Answer;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.AnswerForUi;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.AnswersContainer;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.LabeledUri;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.*;

/**
 * This class contains methods for answer extraction.
 */

public class AnswerExtraction {

    private Word2VecMethods w2vm;

    public AnswerExtraction(){
        this.w2vm = new Word2VecMethods();
    }

    public AnswersContainer extractAnswers(String question, String type, List<ResultEntity> entities, List<ResultTriple> triples){

        AnswersContainer ac = new AnswersContainer();
        ac.setError(false);
        ArrayList<Answer> answers = new ArrayList<>();
        List<AnswerForUi> sortedAnswers = new ArrayList<>();
        if(type.equals("boolean")){
            sortedAnswers = new ArrayList<>();
            sortedAnswers.add(answerBoolean(question,triples));
        } else if (type.startsWith("literal")){
            answers.addAll(answerLiteralWithTopEntities(type.substring(type.lastIndexOf('_')+1),entities));
            sortedAnswers = sortAnswers(question,answers);
        } else if(type.equals("resource")){
            answers.addAll(answerResourceWithTopEntities(type,entities));
            sortedAnswers = sortAnswers(question,answers);
        } else {
            ac.setError(true);
        }

        if(sortedAnswers.size() == 0){
            ac.setTopAnswer(null);
            ac.setAnswers(null);
        } else if (sortedAnswers.size() == 1){
            ac.setTopAnswer(sortedAnswers.get(0));
            ac.setAnswers(null);
        } else {
            ac.setTopAnswer(sortedAnswers.remove(0));
            ac.setAnswers(sortedAnswers);
        }

        return ac;
    }

    public List<AnswerForUi> sortAnswers(String question, ArrayList<Answer> answers){

        Map<String,AnswerForUi> distinctAnswers = new HashMap<>();
        for(Answer ans:answers){
            if(distinctAnswers.containsKey(ans.getAnswerString())){
                distinctAnswers.put(ans.getAnswerString(),joinAnswers(ans,distinctAnswers.get(ans.getAnswerString())));
            } else {
                distinctAnswers.put(ans.getAnswerString(),new AnswerForUi(ans.getAnswerString(),ans.getAnswerType(),ans.getCategory(), Collections.singletonList(ans.getFromEntity()), Collections.singletonList(ans.getFromPredicate()),ans.getScore()));
            }
        }

        List<AnswerForUi> sortedDistinctAnswers = new ArrayList<>(distinctAnswers.values());

        INDArray qVector = w2vm.sentenceVector(question);

        for(AnswerForUi ans:sortedDistinctAnswers){
            /*
            For each predicate find similarity and add it to the answers score
             */
            for(LabeledUri lu:ans.getFromPredicates()){
                INDArray vec = w2vm.sentenceVector(lu.getLabel());
                double sim = Transforms.cosineSim(qVector,vec);
                if(Double.isNaN(sim))
                    sim = 0.0;
                Double extra = 100*sim;
                ans.setScore(extra.intValue()+ans.getScore());
            }
        }

        Collections.sort(sortedDistinctAnswers, new Comparator<AnswerForUi>() {
            @Override
            public int compare(AnswerForUi o1, AnswerForUi o2) {
                return o2.getScore()-o1.getScore();
            }
        });

        return sortedDistinctAnswers;
    }

    public ArrayList<Answer> answerResourceWithTopEntities(String type, List<ResultEntity> entities){
        ArrayList<Answer> answers = new ArrayList<>();
        int score = entities.size();
        for(ResultEntity entity:entities){
            ArrayList<String[]> answersForEntity = SparqlMethods.objectsWithType(entity.getUri(),type);
            for(String[] afe:answersForEntity){
                LabeledUri fromEntity = new LabeledUri(afe[0],entity.getUri());
                LabeledUri fromPredicate = new LabeledUri(afe[2],afe[1]);
                answers.add(new Answer(afe[3],type,"resource",fromEntity,fromPredicate,score));
            }
            score--;
        }

        return answers;
    }

    public ArrayList<Answer> answerLiteralWithTopEntities(String type, List<ResultEntity> entities){
        ArrayList<Answer> answers = new ArrayList<>();
        int score = entities.size();
        for(ResultEntity entity:entities){
            ArrayList<String[]> answersForEntity = SparqlMethods.objectsInRange(entity.getUri(),type);
            for(String[] afe:answersForEntity){
                LabeledUri fromEntity = new LabeledUri(afe[0],entity.getUri());
                LabeledUri fromPredicate = new LabeledUri(afe[2],afe[1]);
                answers.add(new Answer(afe[3],type,"literal",fromEntity,fromPredicate,score));
            }
            score--;
        }
        return answers;
    }

    public AnswerForUi answerBoolean(String question, List<ResultTriple> triples){
        boolean ans = false;

        String[] tokens = question.toLowerCase().split(" ");
        //ResultTriple rightTriple = null;
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
                //rightTriple = triple;
                break;
            }
        }
        LabeledUri empty = new LabeledUri("","");
        if(ans){
            return new AnswerForUi("Yes","boolean","boolean",Arrays.asList(empty),Arrays.asList(empty),1);
        } else{
            return new AnswerForUi("No","boolean","boolean",Arrays.asList(empty),Arrays.asList(empty),1);
        }
    }

    public AnswerForUi joinAnswers(Answer a1, AnswerForUi a2){
        ArrayList<LabeledUri> fromEntities = new ArrayList<>(a2.getFromEntities());
        if(!fromEntities.contains(a1.getFromEntity())){
            fromEntities.add(a1.getFromEntity());
        }
        ArrayList<LabeledUri> fromPredicates = new ArrayList<>(a2.getFromPredicates());
        if(!fromPredicates.contains(a1.getFromPredicate())){
            fromPredicates.add(a1.getFromPredicate());
        }
        int score = a1.getScore()+a2.getScore();
        return new AnswerForUi(a1.getAnswerString(),a1.getAnswerType(),a1.getCategory(),fromEntities,fromPredicates,score);
    }

}