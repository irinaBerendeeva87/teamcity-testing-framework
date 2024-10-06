package com.example.teamcity.api.requests;

import com.example.teamcity.api.enums.Endpoint;
import io.restassured.specification.RequestSpecification;

public class Request  {
    /**
     * Request it's a class describing request parameters such as: specification, endpoint(relative URL, model)
     */
    protected final RequestSpecification spec;
    protected final Endpoint endpoint;

    public Request(RequestSpecification spec, Endpoint endpoint) {
        this.spec = spec;
        this.endpoint = endpoint;
    }
}
