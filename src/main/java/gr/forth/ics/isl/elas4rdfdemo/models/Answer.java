package gr.forth.ics.isl.elas4rdfdemo.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Answer {
    private String answerString;
    private JSONObject tripleOrigin;
    private ArrayList<String> relevantKeywords;
    private double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Answer(String answerString, JSONObject tripleOrigin, ArrayList<String> relevantKeywords, double score) {
        this.answerString = answerString;
        this.tripleOrigin = tripleOrigin;
        this.relevantKeywords = relevantKeywords;
        this.score = score;
    }

    public String getAnswerString() {
        return answerString;
    }

    public void setAnswerString(String answerString) {
        this.answerString = answerString;
    }

    public JSONObject getTripleOrigin() {
        return tripleOrigin;
    }

    public void setTripleOrigin(JSONObject tripleOrigin) {
        this.tripleOrigin = tripleOrigin;
    }

    public ArrayList<String> getRelevantKeywords() {
        return relevantKeywords;
    }

    public void setRelevantKeywords(ArrayList<String> relevantKeywords) {
        this.relevantKeywords = relevantKeywords;
    }

    public JSONObject toJSON(){
        JSONObject jo = new JSONObject();
        jo.put("answer",answerString);
        jo.put("tripleOrigin",tripleOrigin);
        JSONArray ja = new JSONArray();
        for(String s : relevantKeywords){
            ja.put(s);
        }
        jo.put("relevantKeywords",ja);
        jo.put("score",String.valueOf(score));

        return jo;
    }

    public String optUri(String s){
        if(s.startsWith("http"))
            return s;
        else
            return "";
    }

    public String uriToString(String uri){
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

    public String originPretty(){
        return tripleOrigin.getString("sub") + " - " + tripleOrigin.getString("pre") + " - " + tripleOrigin.getString("obj");
    }

    public String relevantKeywordsPretty(){
        return String.join(", ",relevantKeywords);
    }
}
