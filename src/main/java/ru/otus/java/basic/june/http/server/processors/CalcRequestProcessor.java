package ru.otus.java.basic.june.http.server.processors;

import ru.otus.java.basic.june.http.server.HttpRequest;
import ru.otus.java.basic.june.http.server.HttpServer;
import ru.otus.java.basic.june.http.server.exceptions_handling.BadRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CalcRequestProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (!request.containsParameter("a")) {
            throw new BadRequestException("REQUEST_VALIDATION_ERROR", "Обязательный параметр 'a' отсутствует");
        }
        if (!request.containsParameter("b")) {
            throw new BadRequestException("REQUEST_VALIDATION_ERROR", "Обязательный параметр 'b' отсутствует");
        }
        int a = Integer.parseInt(request.getParameter("a"));
        int b = Integer.parseInt(request.getParameter("b"));
        String result = a + " + " + b + " = " + (a + b);
        String response = HttpServer.htmlResponseBuild("200 OK", "<h1>" + result + "</h1>");
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
