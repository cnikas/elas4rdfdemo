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

    public String findImageUrl(){
        String url="";
        String baseURL = "https://en.wikipedia.org/w/api.php?action=query&titles="+uriToString(entity)+"&prop=pageimages&format=json&pithumbsize=100";
        try {
            HttpClient client = HttpClientBuilder.create().build();
            URIBuilder builder = new URIBuilder(baseURL);

            HttpGet request = new HttpGet(builder.build());
            request.addHeader(CONTENT_TYPE, "application/json");

            HttpResponse response = client.execute(request);

            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject responseObject = new JSONObject(json_string);
            if(responseObject != null){
                Iterator<String> ijo = responseObject.getJSONObject("query").getJSONObject("pages").keys();
                if(ijo.hasNext()){
                    JSONObject jo = responseObject.getJSONObject("query").getJSONObject("pages").getJSONObject(ijo.next());
                    if(jo.has("thumbnail")){
                        url = jo.getJSONObject("thumbnail").getString("source");
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static void main(String[] args){
        ResultEntity re = new ResultEntity("Greece","greece");
        System.out.println(re.findImageUrl());
    }

}
