package ru.otus.java.basic.june.http.server.processors;

import ru.otus.java.basic.june.http.server.HttpRequest;
import ru.otus.java.basic.june.http.server.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DefaultNotFoundRequestProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = HttpServer.htmlResponseBuild("404 Not Found",
                "<h1><img src=\"notFound.png\"\n</h1><h1>PAGE NOT FOUND!!!!!!!!!!!!!!!</h1>");
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
