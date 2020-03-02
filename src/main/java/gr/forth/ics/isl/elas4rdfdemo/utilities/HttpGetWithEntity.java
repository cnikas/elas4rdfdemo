package gr.forth.ics.isl.elas4rdfdemo.utilities;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/*
* This class is used to create HTTP GET requests with a body.
 */
public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "GET";

    public HttpGetWithEntity(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpGetWithEntity(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
