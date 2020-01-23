package io.openliberty.guides.restaurant.client;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.NewOrder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class ServeClient { //FIXME WHOLE THING

    @Inject
    @ConfigProperty(name = "SERVE_SERVICE_BASE_URI", defaultValue = "http://localhost:9082")
    private String baseUri;

    private WebTarget target;

    public ServeClient() {
        this.target = null;
    }

    public Response getReady2Serve(){ //FIXME
        return Response.ok().build();
    }

    public Response serveOrder(String orderID){ //FIXME
        return Response.ok().build();
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
                    .path("/orders");
        }
        return this.target;
    }
}