package api.requests;

import api.enums.Endpoint;
import io.restassured.specification.RequestSpecification;

public class Request {
    protected final RequestSpecification spec;
    protected final Endpoint endpoint;

    public Request(RequestSpecification spec, Endpoint endpoint) {
        this.spec = spec;
        this.endpoint = endpoint;
    }
}
