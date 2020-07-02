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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnswerTypePrediction {

    public String predictType(String question) {

        String category = "", type = "";
        String q = question.toLowerCase();
        List<String> qTokens = Arrays.asList(q.split(" "));
        JSONObject bertResult = queryBert(q.replaceAll("[^A-Za-z0-9 ]", ""));

        if(bertResult == null){
            return "";
        }

        category = bertResult.getString("category");
        type = bertResult.getString("type");
        if(category.equals("resource"))
            type = "http://dbpedia.org/ontology/"+type.substring(type.lastIndexOf(":")+1);

        return type;
    }

    public static JSONObject queryBert(String query) {

        String result = "";
        String baseURL = "http://83.212.115.125:80/classify";
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {

            URIBuilder builder = new URIBuilder(baseURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("q", query));
            builder.setParameters(params);
            HttpGet request = new HttpGet(builder.build());
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);
            }
            response.close();
            httpClient.close();
        } catch (IOException | URISyntaxException e) {
            //e.printStackTrace();
        }
        JSONObject resultObject;
        try{
            resultObject = new JSONObject(result);
        } catch (JSONException e){
            resultObject = null;
        }

        return resultObject;
    }

    public static String uriToString(String u){
        String s;
        if(u.startsWith("http"))
            s = u.substring(u.lastIndexOf("/")+1);
        else
            s = u;
        return s;
    }
}
