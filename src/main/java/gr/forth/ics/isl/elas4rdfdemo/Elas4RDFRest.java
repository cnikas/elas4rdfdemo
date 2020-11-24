package gr.forth.ics.isl.elas4rdfdemo;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;


import gr.forth.ics.isl.elas4rdfdemo.utilities.HttpGetWithEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static gr.forth.ics.isl.elas4rdfdemo.Main.props;

/**
 * This class contains methods to use the Elas4RDF Rest api
 */
public class Elas4RDFRest {

    private static final String baseURL = props.getProperty("elas4rdfurl");
    private final HttpClient client;

    public Elas4RDFRest() {
        client = HttpClientBuilder.create().build();
    }

    public JSONObject exactSubjectTypeRequest(String subject) {
        String jsonString = "{\n" +
                "   \"query\":{\n" +
                "      \"bool\":{\n" +
                "         \"must\":[\n" +
                "            {\n" +
                "               \"constant_score\":{\n" +
                "                  \"filter\":{\n" +
                "                     \"term\":{\n" +
                "                        \"subjectTerms\":\"" + subject + "\"\n" +
                "                     }\n" +
                "                  }\n" +
                "               }\n" +
                "            },\n" +
                "            {\n" +
                "               \"constant_score\":{\n" +
                "                  \"filter\":{\n" +
                "                     \"term\":{\n" +
                "                        \"predicateTerms\":\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"\n" +
                "                     }\n" +
                "                  }\n" +
                "               }\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   }\n" +
                "}";

        return generalRequestWithBody(jsonString, "terms_bindex_v2", 50);

    }

    public JSONObject simpleSearch(String query, int size, String type) {
        JSONObject responseObject = null;

        try {
            URIBuilder builder = new URIBuilder(baseURL + "high-level/");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", props.getProperty("datasetId")));
            params.add(new BasicNameValuePair("size", String.valueOf(size)));
            params.add(new BasicNameValuePair("type", type));
            params.add(new BasicNameValuePair("query", query));
            params.add(new BasicNameValuePair("highlightResults", "false"));
            builder.setParameters(params);

            HttpGet request = new HttpGet(builder.build());
            request.addHeader(CONTENT_TYPE, "application/json");

            HttpResponse response = client.execute(request);

            String json_string = EntityUtils.toString(response.getEntity());
            try {
                new JSONObject(json_string);
            } catch (JSONException ex) {
                return null;
            }
            responseObject = new JSONObject(json_string);
            return responseObject;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject generalRequestWithBody(String body, String index, int size) {

        JSONObject responseObject = null;

        try {
            URIBuilder builder = new URIBuilder(baseURL + "low-level/");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("index", index));
            params.add(new BasicNameValuePair("size", String.valueOf(size)));
            params.add(new BasicNameValuePair("type", "triples"));
            params.add(new BasicNameValuePair("highlightResults", "false"));
            builder.setParameters(params);

            HttpGetWithEntity termsRequest = new HttpGetWithEntity(builder.build());
            termsRequest.addHeader(CONTENT_TYPE, "application/json");

            termsRequest.setEntity(new StringEntity(body));

            HttpResponse response = client.execute(termsRequest);

            String json_string = EntityUtils.toString(response.getEntity());
            try {
                new JSONObject(json_string);
            } catch (JSONException ex) {
                return null;
            }
            responseObject = new JSONObject(json_string);
            return responseObject;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}