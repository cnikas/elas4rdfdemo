package gr.forth.ics.isl.elas4rdfdemo.qa.models;

import java.util.ArrayList;
import java.util.List;

public class AnswerForUi {
    private String answerString;
    private String answerType;
    private String category;
    private List<LabeledUri> fromEntities;
    private List<LabeledUri> fromPredicates;
    private int score;

    public AnswerForUi(String answerString, String answerType, String category, List<LabeledUri> fromEntities, List<LabeledUri> fromPredicates, int score) {
        this.answerString = answerString;
        this.answerType = answerType;
        this.category = category;
        this.fromEntities = fromEntities;
        this.fromPredicates = fromPredicates;
        this.score = score;
    }

    public List<LabeledUri> getFromEntities() {
        return fromEntities;
    }

    public void setFromEntities(List<LabeledUri> fromEntities) {
        this.fromEntities = fromEntities;
    }

    public List<LabeledUri> getFromPredicates() {
        return fromPredicates;
    }

    public void setFromPredicates(List<LabeledUri> fromPredicates) {
        this.fromPredicates = fromPredicates;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String entitiesHtml(){
        ArrayList<String> html = new ArrayList<>();
        for(LabeledUri lu:fromEntities){
            html.add(labeledUriToHtml(lu));
        }
        return String.join(", ",html);
    }

    public String getAnswerString() {
        return answerString;
    }

    public void setAnswerString(String answerString) {
        this.answerString = answerString;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String predicatesHtml(){
        ArrayList<String> html = new ArrayList<>();
        for(LabeledUri lu:fromPredicates){
            html.add(labeledUriToHtml(lu));
        }
        return String.join(", ",html);
    }

    public String labeledUriToHtml(LabeledUri lu){
        return "<a href=\""+lu.getUri()+"\">"+uriToString(lu.getLabel())+"</a>";
    }

    public String spanOrLink(String s){
        if(s.startsWith("http")){
            return "<a href=\""+s+"\">"+uriToString(s)+"</a>";
        } else {
            return "<span>"+s+"</span>";
        }
    }
    /*
     * Converts a uri or literal to a human-readable string
     */
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
}
