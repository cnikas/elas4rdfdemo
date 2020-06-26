package gr.forth.ics.isl.elas4rdfdemo.models;

import java.text.DecimalFormat;

public class ResultEntity {
    public String ext;
    public String entity;
    public double score;
    private int frequency;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public double getScore() {
        return score;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getFrequencyString() {
        if(this.frequency == 1){
            return "in 1 triple";
        } else {
            return "in "+this.frequency+" triples";
        }
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getScoreClean() {

        DecimalFormat df = new DecimalFormat("0.000");
        String angleFormated = df.format(this.score);
        return angleFormated;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String imageUrl;

    public ResultEntity(String ext, String entity, double score){
        this.ext = ext;
        this.entity = entity;
        this.score= score;
        this.frequency = 0;
    }

    public ResultEntity(String ext, String entity, double score,int frequency){
        this.ext = ext;
        this.entity = entity;
        this.score= score;
        this.frequency = frequency;
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

        if(clean.length() > 1){
            clean = Character.toUpperCase(clean.charAt(0)) + clean.substring(1);

            for (int i = 0; i < clean.length(); i++){
                char c = clean.charAt(i);
                if(c == '_'){
                    clean = clean.substring(0,i) + "_" + Character.toUpperCase(clean.charAt(i+1)) + clean.substring(i+2);
                }
            }
        }
        return clean.trim();
    }

    public String getUriLabel(){
        return uriToString(this.entity);
    }

    public String shorten(String s){
        if(s.length() > 280)
            return s.substring(0,280)+"...";
        else
            return s;
    }

    public String extClean(){
        String exc = this.ext;
        if(exc.startsWith("[") && exc.endsWith("]"))
            return exc.substring(1,exc.length()-1);
        else
            return exc;
    }

}
