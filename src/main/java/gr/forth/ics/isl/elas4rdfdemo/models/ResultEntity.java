package gr.forth.ics.isl.elas4rdfdemo.models;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class ResultEntity {
    public String ext;
    public String entity;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String imageUrl;

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

    public static void main(String[] args){
    }

}
