package gr.forth.ics.isl.elas4rdfdemo;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import gr.forth.ics.isl.utilities.HttpGetWithEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Elas4RDFRest {

    private static final String baseURL = "http://139.91.183.46:8080/elas4rdf_rest/";
    private final HttpClient client;

    public Elas4RDFRest() {
        client = HttpClientBuilder.create().build();
    }

    public JSONObject executeConstantScoreRequest(String query, String index, int size){
        String jsonString = "{\"query\" : {\"constant_score\" : {\"filter\" : {\"term\" : {\"subjectTerms\":\""+query+"\"}}}}}";

        return generalRequestWithBody(jsonString,index,size);
    }

    public JSONObject executeQuery(String query, String index, int size){

        String jsonString = "{\"query\":{\"query_string\":{\"query\" : \""+query+"\"}}}";

        return generalRequestWithBody(jsonString,index,size);
    }

    public JSONObject executeMultiMatchQuery(String firstTerm, String secondTerm, String index, int size,String type){
        String field = "subjectKeywords";
        if(type.equals("obj")){
            field = "objectKeywords";
        }

        String jsonString = "{\"query\": { \"multi_match\" : {\"query\": \""+firstTerm+" "+secondTerm+"\", \"fields\": [ \""+field+"\", \"predicateKeywords\"] ,\"type\" : \"cross_fields\"}}}";

        return generalRequestWithBody(jsonString,index,size);
    }

    public JSONObject checkUriForTermRequest(String query, String index, int size){

        String jsonString = "{\"query\": {\"match\" : {\"subjectKeywords\" : {\"query\" : \""+query+"\"}}}}";

        return generalRequestWithBody(jsonString,index,size);
    }

    public JSONObject checkType(String query){

        String jsonString = "{\"query\": {\"query_string\" : {\"query\" : \"+subjectKeywords:("+query+") +predicateKeywords:(type)\"}}}";

        JSONObject responseObject = null;

        try {
            URIBuilder builder = new URIBuilder(baseURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("index", "terms_bindex"));
            params.add(new BasicNameValuePair("size", String.valueOf(10)));
            builder.setParameters(params);

            HttpGetWithEntity termsRequest = new HttpGetWithEntity(builder.build());
            termsRequest.addHeader(CONTENT_TYPE, "application/json");

            termsRequest.setEntity(new StringEntity(jsonString));

            HttpResponse response = client.execute(termsRequest);

            String json_string = EntityUtils.toString(response.getEntity());
            //System.out.println(json_string);
            responseObject = new JSONObject(json_string);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    public JSONObject queryExtFields(String query, String type, int size){

        String jsonString = "{\"query\": {\"match\" : {\"rdfs_comment_"+type+"\" : {\"query\" : \""+query+"\"}}}}";

        return generalRequestWithBody(jsonString,"terms_eindex", size);
    }

    public JSONObject multiMatchQueryForType(String uri, int size){
        String jsonString = "{\"query\": { \"multi_match\" : {\"query\": \""+uri+" type\", \"fields\": [ \"subjectKeywords\", \"predicateKeywords\"] ,\"type\" : \"cross_fields\"}}}";

        return generalRequestWithBody(jsonString,"terms_bindex", size);
    }

    public JSONObject simpleSearch(String query, int size, String index, String type){
        JSONObject responseObject = null;

        try {
            URIBuilder builder = new URIBuilder(baseURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("index", index));
            params.add(new BasicNameValuePair("size", String.valueOf(size)));
            params.add(new BasicNameValuePair("type", type));
            params.add(new BasicNameValuePair("query", query));
            builder.setParameters(params);

            HttpGet request = new HttpGet(builder.build());
            request.addHeader(CONTENT_TYPE, "application/json");

            HttpResponse response = client.execute(request);

            String json_string = EntityUtils.toString(response.getEntity());
            responseObject = new JSONObject(json_string);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return responseObject;
    }

    public JSONObject generalRequestWithBody(String body, String index, int size){

        JSONObject responseObject = null;

        try {
            URIBuilder builder = new URIBuilder(baseURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("index", index));
            params.add(new BasicNameValuePair("size", String.valueOf(size)));
            params.add(new BasicNameValuePair("type", "triples"));
            builder.setParameters(params);

            HttpGetWithEntity termsRequest = new HttpGetWithEntity(builder.build());
            termsRequest.addHeader(CONTENT_TYPE, "application/json");

            termsRequest.setEntity(new StringEntity(body));

            HttpResponse response = client.execute(termsRequest);

            String json_string = EntityUtils.toString(response.getEntity());
            responseObject = new JSONObject(json_string);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return responseObject;
}


}