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
        type = bertResult.getString("class");
        if(!type.equals(""))
            type = "http://dbpedia.org/ontology/"+type.substring(type.lastIndexOf(":")+1);

        if (category.equals("literal")) {
            if (q.startsWith("when")) {
                type = "literal_date";
            }
            List<String> numberLiteralKeys = Arrays.asList("score", "rate", "number", "profit", "distance", "mean", "count", "coefficient", "count", "amount", "population", "cost", "length", "unit", "percentage","total");
            for (String token: qTokens) {
                if (numberLiteralKeys.contains(token)) {
                    type = "literal_number";
                }
            }
            List<String> temporalLiteralKeys = Arrays.asList("year", "time", "date", "birthday", "birthdays");
            for (String token:qTokens) {
                if (temporalLiteralKeys.contains(token)) {
                    type = "literal_date";
                }
            }
        }

        if (category.equals("literal") || category.equals("resource")) {
            List<String> stringLiteralKeys = Arrays.asList("id", "prefix", "code", "nickname");
            for (String token: qTokens) {
                if (stringLiteralKeys.contains(token)) {
                    type = "literal_string";
                    category = "literal";
                }
            }

            if (question.toLowerCase().contains("how many") || question.toLowerCase().contains("how much")) {
                type = "literal_number";
                category = "literal";
            }
        }

        if (category.equals("literal") && type.equals("")) {
            type = "literal_string";
        }

        return type;
    }

    public static JSONObject queryBert(String query) {

        String result = "";
        String baseURL = "http://127.0.0.1:5000/classify";
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
