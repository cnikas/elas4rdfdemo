package gr.forth.ics.isl.elas4rdfdemo.models;

public class ResultEntity {
    public String ext;
    public String entity;

    public ResultEntity(String ext, String entity){
        this.ext = ext;
        this.entity = entity;
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

    public String shorten(String s){
        if(s.length() > 280)
            return s.substring(0,280)+"...";
        else
            return s;
    }

}
