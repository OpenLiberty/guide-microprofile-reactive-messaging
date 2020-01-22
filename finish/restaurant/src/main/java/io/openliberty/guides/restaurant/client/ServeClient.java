package io.openliberty.guides.restaurant.client;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ServeClient {

    @Inject
    @ConfigProperty(name = "SERVE_SERVICE_BASE_URI", defaultValue = "http://localhost:9082") //FIXME
    private String baseUri;

    private WebTarget target;

    public Response getReady2Serve(){
        return iBuilder(webTarget())
                .get();
    }

    public Response serveOrder(String orderID){
        return null;
    }

    private Invocation.Builder iBuilder(WebTarget target) {
        return target
                .request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    //Sets target to endpoint provided by Order API
    private WebTarget webTarget() {
        if (this.target == null) {
            this.target = ClientBuilder
                    .newClient()
                    .target(baseUri)
                    .path("/orders"); //FIXME
        }
        return this.target;
    }
}
