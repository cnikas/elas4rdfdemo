package gr.forth.ics.isl.elas4rdfdemo.qa;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to query dbpedia spotlight's rest api
 */
public class Spotlight {
    public static String querySpotlight(String text, String types, double confidence) {

        String result = "";
        String baseURL = "http://api.dbpedia-spotlight.org/en/annotate";
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {

            URIBuilder builder = new URIBuilder(baseURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("text", text));
            params.add(new BasicNameValuePair("confidence", Double.toString(confidence)));
            params.add(new BasicNameValuePair("types", types));
            builder.setParameters(params);
            HttpGet request = new HttpGet(builder.build());

            request.addHeader("Accept", "application/json");

            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);
            }
            response.close();
            httpClient.close();

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getTypes(String response){
        try{
            JSONObject res = new JSONObject(response);

            if(res.optJSONArray("Resources") != null){
                return res.getJSONArray("Resources").getJSONObject(0).getString("@types");
            } else {
                System.err.println("Spotlight found no annotation");
                return "";
            }
        } catch(org.json.JSONException e){
            System.err.println("Erroneous response from DBPedia Spotlight");
            System.err.println(response);
            return "";
        }
    }

    public static List<String> tryForSpotlightAnnotations(String query){

        int attempt=1, maxAttempts = 3;
        List<String> uris = new ArrayList<>();
        while(true){
            try{
                String response = querySpotlight(query,"",0.35);
                JSONObject res = new JSONObject(response);

                if(res.optJSONArray("Resources") != null){
                    for(int i=0;i<res.getJSONArray("Resources").length();i++){
                        uris.add(res.getJSONArray("Resources").getJSONObject(i).getString("@URI"));
                    }
                }
            } catch(org.json.JSONException e){
                if(attempt==maxAttempts){
                    break;
                }
                attempt++;
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return uris;
    }

}