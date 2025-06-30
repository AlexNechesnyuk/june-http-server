package ru.otus.java.basic.june.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String rawRequest;
    private String uri;
    private long hash;
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

    public Long getRoutingKey() {
        return hash;
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
        logger.info("URI: " + uri);
        logger.info("BODY: " + body);
    }

    private long hashParse() {
        long rc = 0;
        int tokenCount = 0;

        int i = 0;
        for (; i < rawRequest.length() && tokenCount < 2; i++) {
            char c = rawRequest.charAt(i);
            if (c == ' ' || c == '?') {
                tokenCount++;
            }
        }
        rc = computeHash(rawRequest, 0, i - 1);
        return rc;
    }

    static public long computeHash(String rawRequest) {
        int i = 0, foundSpaces = 0;
        for (; i < rawRequest.length(); i++) {
            if (rawRequest.charAt(i) == ' ' || rawRequest.charAt(i) == '?') {
                if (foundSpaces == 1) {
                    return computeHash(rawRequest, 0, i);
                }
                foundSpaces++;
            }
        }
        return 0;
    }

    static public long computeHashFromString(String rawRequest) {
        return computeHash(rawRequest, 0, rawRequest.length());
    }

    static public long computeHash(String rawRequest, int start, int end) {
        long rc = 0;
        for (int i = start; i < end; i++) {
            rc = Long.rotateLeft(rc, 5);
            rc ^= (int) rawRequest.charAt(i);
        }
        return rc;
    }

    private void doParse() {
        hash = computeHash(rawRequest);
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
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
        if (rawRequest.startsWith("POST"))
            body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
    }
}











