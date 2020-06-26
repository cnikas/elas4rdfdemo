package gr.forth.ics.isl.elas4rdfdemo.qa.models;

/*
*Model class for an answer retrieved by the system
 */
public class Answer {
    private String answerString;
    private String answerType;
    private String originType;
    private String originUri;

    public Answer(String answerString, String answerType, String originType, String originUri) {
        this.answerString = answerString;
        this.answerType = answerType;
        this.originType = originType;
        this.originUri = originUri;
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

    /*
    * Returns the origin triple in html.
     */
    public String originPretty(){
        return "From "+originType+" <a href="+originUri+">"+uriToString(originUri)+"</a>";
    }

    @Override
    public String toString() {
        return getAnswerString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Answer)) return false;
        Answer other = (Answer) o;
        return this.getAnswerString().equals(other.getAnswerString());
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

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public String getOriginUri() {
        return originUri;
    }

    public void setOriginUri(String originUri) {
        this.originUri = originUri;
    }
}
