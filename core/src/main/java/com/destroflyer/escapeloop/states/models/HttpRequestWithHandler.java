package com.destroflyer.escapeloop.states.models;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HttpRequestWithHandler<Request, Response> {
    private String method;
    private String url;
    private String authorization;
    private Request requestBody;
    private Class<Response> responseBodyClass;
    private Consumer<Response> onSuccess;
}
