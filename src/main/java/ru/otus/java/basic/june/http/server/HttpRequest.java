package ru.otus.java.basic.june.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String rawRequest;
    private String method;
    private String uri;
    private Map<String, String> parameters;
    private String body;
    private static final Logger logger = LogManager.getLogger(HttpRequest.class);

    private HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.parameters = new HashMap<>();
    }

    public static HttpRequest parse(String rawRequest) {
        HttpRequest httpRequest = new HttpRequest(rawRequest);
        httpRequest.doParse();
        return httpRequest;
    }

    public String getUri() {
        return uri;
    }

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public String getBody() {
        return body;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }


    public void info() {
        logger.debug(rawRequest);
        logger.info("METHOD: " + method);
        logger.info("METHOD: " + method);
        logger.info("URI: " + uri);
        logger.info("BODY: " + body);
    }

    private void doParse() {
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
        method = rawRequest.substring(0, startIndex);
        uri = rawRequest.substring(startIndex + 1, endIndex);
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            uri = elements[0];
            String[] keysValues = elements[1].split("[&]");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
    }
}











