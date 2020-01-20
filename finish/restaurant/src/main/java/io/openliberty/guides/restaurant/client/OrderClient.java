package io.openliberty.guides.restaurant.client;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class OrderClient {

    @Inject
    @ConfigProperty(name = "ORDER_SERVICE_BASE_URI", defaultValue = "http://localhost:9082")
    private String baseUri;

    private WebTarget target;

    public OrderClient() {
        this.target = null;
    }
    
    public Response createDrink(){
        return iBuilder(webTarget().path("drink"))
                .post(null);
    }

    public Response createFood(){
        return iBuilder(webTarget().path("food"))
                .post(null); //TODO Submit a POST body with params
    }

    private Invocation.Builder iBuilder(WebTarget target) {
        return target
                .request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    private WebTarget webTarget() {
        if (this.target == null) {
            this.target = ClientBuilder
                    .newClient()
                    .target(baseUri)
                    .path("/orders");
        }
        return this.target;
    }
}