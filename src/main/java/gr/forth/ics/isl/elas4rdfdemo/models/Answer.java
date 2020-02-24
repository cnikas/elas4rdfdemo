package gr.forth.ics.isl.elas4rdfdemo.models;

import org.apache.jena.atlas.json.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Answer {
    private String answerString;
    private JSONObject tripleOrigin;
    private ArrayList<String> relevantKeywords;

    public Answer(String answerString, JSONObject tripleOrigin, ArrayList<String> relevantKeywords) {
        this.answerString = answerString;
        this.tripleOrigin = tripleOrigin;
        this.relevantKeywords = relevantKeywords;
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

        return jo;
    }
}
