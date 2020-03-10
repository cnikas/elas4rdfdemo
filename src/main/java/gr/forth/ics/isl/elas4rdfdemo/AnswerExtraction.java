package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.*;
import gr.forth.ics.isl.elas4rdfdemo.utilities.StringUtilsSimple;
import org.json.JSONArray;
import org.json.JSONObject;

import static gr.forth.ics.isl.elas4rdfdemo.Main.props;
import java.util.*;

public class AnswerExtraction {

    public Elas4RDFRest elas4RDF;
    QuestionAnalysis qa;

    public AnswerExtraction() {
        qa = new QuestionAnalysis();
        elas4RDF = new Elas4RDFRest();
    }

    public AnswersContainer extractAnswers(String query){

        AnswersContainer ac = new AnswersContainer();

        ParsedQuestion q = qa.analyzeQuestion(query);

        ArrayList<Answer> answers = new ArrayList<>();

        answers.addAll(extractAnswerWithEntity(q));

        answers.addAll(extractAnswerWithQuery(q));

        answers.addAll(extractAnswerWithExtFields(q));

        Comparator<Answer> ansComp = Comparator.comparingDouble(Answer::getScore).reversed();
        Collections.sort(answers,ansComp);

        if(!q.isList() && answers.size() > 0){
            ac.setTopAnswer(answers.remove(0));
        }
        ac.setAnswers(answers);
        ac.setType(q.getqType());
        ac.setList(q.isList());
        return ac;
    }

    public JSONObject extractAnswerJson(ParsedQuestion q){

        JSONObject jobj = new JSONObject();

        jobj.put("question",q.getQuestion());

        ArrayList<Answer> answers1 = extractAnswerWithEntity(q);

        JSONArray ansArray1 = new JSONArray();
        for(Answer ans : answers1){
            ansArray1.put(ans.toJSON());
        }
        jobj.put("entity_answers",ansArray1);

        ArrayList<Answer> answers2 = extractAnswerWithQuery(q);
        JSONArray ansArray2 = new JSONArray();
        for(Answer ans : answers2){
            ansArray2.put(ans.toJSON());
        }
        jobj.put("query_answers",ansArray2);

        ArrayList<Answer> answers3 = extractAnswerWithExtFields(q);
        JSONArray ansArray3 = new JSONArray();
        for(Answer ans : answers3){
            ansArray3.put(ans.toJSON());
        }
        jobj.put("ext_field_answers",ansArray3);

        return jobj;
    }

    /*
    *For each Keyword, check if a uri exists, then find the relevant triples from the results, and
    *add their object to the set of answers.
     */
    public ArrayList<Answer> extractAnswerWithEntity(ParsedQuestion q){

        ArrayList<Answer> candidateAnswers =new ArrayList<>();

        ArrayList<String> terms = new ArrayList<>(q.joinAllTerms());

        for(int i=0; i<terms.size();i++){
            String uri = cleanUriOrLiteral(checkUriForTerm(terms.get(i)));
            if(!uri.equals("")){
                candidateAnswers.addAll(constantScoreCandidateTriples(uri,terms));
                for(int j=i+1; j<terms.size(); j++){
                    candidateAnswers.addAll(multiMatch(uri,terms.get(j),terms));
                }

            }

        }

        return candidateAnswers;

    }

    public ArrayList<Answer> multiMatch(String uri, String term, ArrayList<String> terms){
        ArrayList<Answer> candidateTriples = new ArrayList<>();

        candidateTriples.addAll(multiMatchType(uri,term,terms,"sub"));
        candidateTriples.addAll(multiMatchType(uri,term,terms,"obj"));

        return candidateTriples;
    }

    public ArrayList<Answer> multiMatchType(String uri, String term, ArrayList<String> terms, String type){
        String otherType = "obj";
        if(type.equals("obj")){
            otherType = "sub";
        }
        ArrayList<Answer> candidateTriples =new ArrayList<>();
        JSONObject results = elas4RDF.executeMultiMatchQuery(uri,term,"terms_bindex",Integer.parseInt(props.getProperty("multiMatchQuerySize")),type);

        JSONObject resultsObject = null;
            if(results      != null){
                resultsObject = results.optJSONObject("results");
            }
            JSONArray resultsArray = null;
            if( resultsObject != null){
                resultsArray = resultsObject.optJSONArray("triples");
            }

            if(resultsArray != null){
            for(int i=0; i<resultsArray.length(); i++){
                String foundPredicate = cleanUriOrLiteral(resultsArray.getJSONObject(i).getString("pre"));
                for(String t : terms){
                    if(foundPredicate.toLowerCase().contains(t.toLowerCase().replace(" ",""))){
                        ArrayList<String> relevantTerms  = new ArrayList<>(Arrays.asList(term,t));
                        candidateTriples.add(new Answer(resultsArray.getJSONObject(i).getString(otherType),resultsArray.getJSONObject(i),relevantTerms,resultsArray.getJSONObject(i).getDouble("score")));
                        break;
                    }
                }
            }
        }

        return candidateTriples;
    }

    /*
     *Searches for triples with subject or object "firstTerm" and returns those that have a relevant predicate
     * according to predicateTerms.
     */
    public ArrayList<Answer> constantScoreCandidateTriples(String firstTerm, ArrayList<String> predicateTerms){

        ArrayList<Answer> candidates = new ArrayList<>();

        JSONObject results = elas4RDF.executeConstantScoreRequest(firstTerm,"terms_bindex",Integer.parseInt(props.getProperty("constantScoreQuerySize")));

        JSONObject resultsObject = null;
        if(results      != null){
            resultsObject = results.optJSONObject("results");
        }
        JSONArray resultsArray = null;
        if( resultsObject != null){
            resultsArray = resultsObject.optJSONArray("triples");
        }

        if(resultsArray != null){
            for(int i=0; i<resultsArray.length(); i++){
                String foundPredicate = cleanUriOrLiteral(resultsArray.getJSONObject(i).getString("pre"));
                for(String term : predicateTerms){
                    if(foundPredicate.toLowerCase().equals(term.toLowerCase().replace(" ",""))){
                        ArrayList<String> relevantTerms  = new ArrayList<>(Arrays.asList(firstTerm,term));
                        candidates.add(new Answer(resultsArray.getJSONObject(i).getString("obj"),resultsArray.getJSONObject(i),relevantTerms,resultsArray.getJSONObject(i).getDouble("score")));
                    }
                }
            }
        }

        return candidates;
    }

    public String findTypeFromUri(String uri){
        JSONObject result = elas4RDF.checkType(uri);
        JSONArray resultArray = result.optJSONObject("results").optJSONArray("triples");

        for(int i=0; i<resultArray.length(); i++){
            JSONObject triple = resultArray.getJSONObject(i);
            if(cleanUriOrLiteral(triple.getString("pre")).toLowerCase().equals("type")){
                if(cleanUriOrLiteral(triple.getString("obj")).toLowerCase().equals("person")){
                    return "Person";
                }
                if(cleanUriOrLiteral(triple.getString("obj")).toLowerCase().equals("place")){
                    return "Place";
                }
            }
        }
        return "";
    }

    /*
    *Searches for all keywords and returns subjects that have a relevant sub_ext and objects that
    * have a relevant obj_ext
    *
     */
    public ArrayList<Answer> extractAnswerWithExtFields(ParsedQuestion q){
        ArrayList<Answer> answers =new ArrayList<>();

        ArrayList<String> terms = new ArrayList<>(q.joinAllTerms());

        answers.addAll(extFieldCandidateAnswers(terms,"sub"));
        answers.addAll(extFieldCandidateAnswers(terms,"obj"));

        return answers;
    }

    public ArrayList<Answer> extFieldCandidateAnswers(ArrayList<String> terms, String type){
        String otherType = "obj";
        if(type.equals("obj")){
            otherType = "sub";
        }
        ArrayList<Answer> answers =new ArrayList<>();
        String joinedTerms = String.join(" ",terms);

        JSONObject results = elas4RDF.queryExtFields(joinedTerms,type,Integer.parseInt(props.getProperty("extFieldsQuerySize")));
        JSONObject resultsObject = null;
        if(results      != null){
            resultsObject = results.optJSONObject("results");
        }
        JSONArray resultArray = null;
        if( resultsObject != null){
            resultArray = resultsObject.optJSONArray("triples");
        }
        if(resultArray != null){
            for(int i=0; i<resultArray.length();i++){
                ArrayList<String> relevantTerms  = new ArrayList<>();
                String resultString = resultArray.getJSONObject(i).getJSONObject(type+"_ext").optString("rdfs_comment").toLowerCase();
                int cnt =0;
                for(String term: terms){
                    if(resultString.contains(term.toLowerCase())){
                        cnt++;
                        relevantTerms.add(term);
                    }
                }
                double relevance = (double)cnt/(double)terms.size();
                double threshold = Double.parseDouble(props.getProperty("extFieldsRelevanceThreshold"));
                if(Double.compare(relevance,threshold)>=0){
                    answers.add(new Answer(resultArray.getJSONObject(i).getString(otherType),resultArray.getJSONObject(i),relevantTerms,resultArray.getJSONObject(i).getDouble("score")));
                }
            }
        }

        return answers;
    }


    public ArrayList<Answer> extractAnswerWithQuery(ParsedQuestion q){
        ArrayList<Answer> candidateAnswers = new ArrayList<>();

        candidateAnswers.addAll(candidateAnswersFromQueries(q,"sub"));
        candidateAnswers.addAll(candidateAnswersFromQueries(q,"obj"));

        return candidateAnswers;
    }

    public ArrayList<Answer> candidateAnswersFromQueries(ParsedQuestion q, String type){

        ArrayList<Answer> answers = new ArrayList<>();

        ArrayList<Query> subQueries = buildQueries(q,type);

        for(Query query : subQueries){
            JSONObject results = elas4RDF.executeQuery(query.getQuery(),"terms_bindex",Integer.parseInt(props.getProperty("dslQuerySize")));
            JSONObject resultsObject = null;
            if(results      != null){
                resultsObject = results.optJSONObject("results");
            }
            JSONArray resultsArray = null;
            if( resultsObject != null){
                resultsArray = resultsObject.optJSONArray("triples");
            }
            if(resultsArray != null){
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject triple = resultsArray.getJSONObject(i);
                    Answer ans = relevantResult(query,triple,type);
                    if(ans != null){
                        answers.add(ans);
                    }
                }
            }

        }
        return answers;
    }

    /*
    * Converts a uri or a literal to a plain string.
     */
    public String cleanUriOrLiteral(String uri){
        String clean = "";
        if(uri.startsWith("http")){
            clean = uri.substring(uri.lastIndexOf("/")+1);
        }else if(clean.contains("@")){
            clean = clean.substring(0,clean.indexOf("@"));
        }else{
            clean = uri;
        }
        return clean.trim();
    }

    public Answer relevantResult(Query q, JSONObject triple, String type){
        String otherType = "obj";
        if(type.equals("obj")){
            otherType = "sub";
        }
        ArrayList<String> relevantTerms  = new ArrayList<>();

        String firstTermString = cleanUriOrLiteral(triple.getString(type)).toLowerCase().replaceAll("_"," ").replaceAll("[\\(\\)]","");

        int cnt = 0;
        for(String kw : q.getFirstTermKeywords()){
            if(firstTermString.contains(kw.toLowerCase())){
                cnt++;
                relevantTerms.add(kw);
            }
        }
        double relevance = (double)cnt/(double)q.getFirstTermKeywords().size();
        double threshold = Double.parseDouble(props.getProperty("dslFirstTermRelevanceThreshold"));
        boolean relevantFirstTerm = Double.compare(relevance,threshold) > 0;

        String predicateString = cleanUriOrLiteral(triple.getString("pre")).toLowerCase();
        boolean relevantPredicate = false;
        for(String kw : q.getPredicateKeywords()){
            if(predicateString.equals(kw.toLowerCase().replaceAll(" ",""))){
                relevantPredicate = true;
                relevantTerms.add(kw);
            }

        }

        if(relevantFirstTerm&&relevantPredicate){
            return new Answer(triple.getString(otherType),triple,relevantTerms,triple.getDouble("score"));
        } else {
            return null;
        }
    }

    /*
    * Matches term for URIs in the dataset and returns the most similar one.
    */
    public String checkUriForTerm(String term){
        String foundUri = "";
        JSONObject results = elas4RDF.checkUriForTermRequest(term,"terms_bindex",Integer.parseInt(props.getProperty("checkUriQuerySize")));
        double maxJsim = 0.0;
        JSONObject resultsObject = null;
        if(results != null){
            resultsObject = results.optJSONObject("results");
        }
        JSONArray resultsArray = null;
        if( resultsObject != null){
            resultsArray = resultsObject.optJSONArray("triples");
        }
        if(resultsArray != null){
            for(int i=0;i<resultsArray.length();i++){
                String currUri = cleanUriOrLiteral(resultsArray.getJSONObject(i).getString("sub"));
                if(currUri.replaceAll("_"," ").equalsIgnoreCase(term)){
                    foundUri = currUri;
                } else{
                    double jSim = StringUtilsSimple.JaccardSim(currUri.toLowerCase().split("_"),term.toLowerCase().split(" "));
                    if(Double.compare(jSim,maxJsim)>0){
                        maxJsim = jSim;
                        foundUri = currUri;
                    }
                }
            }

        }
        return foundUri;
    }

    public ArrayList<Query> buildQueries(ParsedQuestion q, String type){
        String firstField = "subjectKeywords";
        if(type.equals("obj")){
             firstField = "objectKeywords";
        }
        ArrayList<Query> queries = new ArrayList<>();

        List<Keyword> keywords = q.getKeyWords();
        for(int i=0; i<keywords.size(); i++){
            for(int j=i+1; j <keywords.size();j++){
                Query q1 = new Query();
                q1.setQuery("+"+firstField+":"+keywords.get(i).generateQueryTerm()+" +predicateKeywords:"+keywords.get(j).generateQueryTerm());
                q1.setFirstTermKeywords(keywords.get(i).setOfAllWords());
                q1.setPredicateKeywords(keywords.get(j).setOfAllWords());

                Query q2 = new Query();
                q2.setQuery("+"+firstField+":"+keywords.get(j).generateQueryTerm()+" +predicateKeywords:"+keywords.get(i).generateQueryTerm());
                q2.setFirstTermKeywords(keywords.get(j).setOfAllWords());
                q2.setPredicateKeywords(keywords.get(i).setOfAllWords());

                queries.add(q1);
                queries.add(q2);
            }
        }
        return queries;
    }



}
