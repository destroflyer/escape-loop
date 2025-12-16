package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.states.models.HttpRequestWithHandler;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class HttpState extends State {

    public HttpState() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
    }
    private static final String LOG_TAG = "HTTP_REQUEST";
    private Json json;
    private ConcurrentLinkedQueue<HttpRequestWithHandler<?, ?>> requestQueue = new ConcurrentLinkedQueue<>();
    private boolean requestQueueLocked;

    public <Request, Response> void queueRequest(String method, String url, String authorization, Request requestBody, Class<Response> responseClass, Consumer<Response> onSuccess) {
        requestQueue.add(new HttpRequestWithHandler<>(method, url, authorization, requestBody, responseClass, onSuccess));
        tryNextRequest();
    }

    private void tryNextRequest() {
        if (!requestQueueLocked && !requestQueue.isEmpty()) {
            request(requestQueue.poll());
        }
    }

    private <Request, Response> void request(HttpRequestWithHandler<Request, Response> requestWithHandler) {
        Net.HttpRequest request = new Net.HttpRequest(requestWithHandler.getMethod());
        request.setUrl(requestWithHandler.getUrl());
        request.setHeader("Authorization", requestWithHandler.getAuthorization());
        String requestBody = null;
        if (requestWithHandler.getRequestBody() != null) {
            requestBody = json.toJson(requestWithHandler.getRequestBody());
            request.setHeader("Content-Type", "application/json");
            request.setContent(requestBody);
        }

        String _requestBody = requestBody;
        BiFunction<String, Integer, String> getDebugMessage = (result, statusCode) ->
            "Http request " + result + " - " + requestWithHandler.getMethod() + " " + requestWithHandler.getUrl()
            + ((statusCode != null) ? " " + statusCode : "")
            + ((_requestBody != null) ? ", body=" + _requestBody : "");
        requestQueueLocked = true;
        Runnable onFinish = () -> Gdx.app.postRunnable(() -> {
            requestQueueLocked = false;
            tryNextRequest();
        });
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String response = httpResponse.getResultAsString();
                if ((statusCode >= 200) && (statusCode < 300)) {
                    if (requestWithHandler.getOnSuccess() != null) {
                        Gdx.app.postRunnable(() -> {
                            Response responseBody = json.fromJson(requestWithHandler.getResponseBodyClass(), response);
                            requestWithHandler.getOnSuccess().accept(responseBody);
                        });
                    }
                    Gdx.app.log(LOG_TAG, getDebugMessage.apply("succeeded", statusCode));
                } else {
                    Gdx.app.error(LOG_TAG, getDebugMessage.apply("failed", statusCode) + ", error = " + response);
                }
                onFinish.run();
            }

            @Override
            public void failed(Throwable throwable) {
                Gdx.app.error(LOG_TAG, getDebugMessage.apply("failed", null) + ", error = " + throwable.toString());
                onFinish.run();
            }

            @Override
            public void cancelled() {
                Gdx.app.error(LOG_TAG, getDebugMessage.apply("cancelled", null));
                onFinish.run();
            }
        });
    }
}
