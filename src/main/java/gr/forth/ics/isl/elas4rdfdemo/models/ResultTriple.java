package gr.forth.ics.isl.elas4rdfdemo.models;

public class ResultTriple {
    public String subject;
    public String predicate;
    public String object;
    public String objExt;
    public String subExt;

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

    public String optObjectUri(){
        if(object.startsWith("http"))
            return object;
        else
            return "";
    }

    public String shorten(String s){
        if(s.length() > 280 && !s.startsWith("http"))
            return s.substring(0,280)+"...";
        else
            return s;
    }

    public ResultTriple(String subject, String predicate, String object, String subExt, String objExt){
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.subExt = subExt;
        this.objExt = objExt;
    }
}
