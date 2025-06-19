package ru.otus.java.basic.june.http.server.processors;

import ru.otus.java.basic.june.http.server.HttpRequest;
import ru.otus.java.basic.june.http.server.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HelloRequestProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = HttpServer.htmlResponseBuild("200 OK", "<h1>Hello World</h1><p>Hello</p><h2>Hello World</h2>");
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
