package gr.forth.ics.isl.elas4rdfdemo.qa.models;

/*
*Model class for an answer retrieved by the system
 */
public class Answer {
    private String answerString;
    private String answerType;
    private String category;
    private LabeledUri fromEntity;
    private LabeledUri fromPredicate;
    private int score;
    private double similarity;

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public Answer(String answerString, String answerType, String category, LabeledUri fromEntity, LabeledUri fromPredicate, int score) {
        this.answerString = answerString;
        this.answerType = answerType;
        this.category = category;
        this.fromEntity = fromEntity;
        this.fromPredicate = fromPredicate;
        this.score = score;
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

    public LabeledUri getFromEntity() {
        return fromEntity;
    }

    public void setFromEntity(LabeledUri fromEntity) {
        this.fromEntity = fromEntity;
    }

    public LabeledUri getFromPredicate() {
        return fromPredicate;
    }

    public void setFromPredicate(LabeledUri fromPredicate) {
        this.fromPredicate = fromPredicate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
