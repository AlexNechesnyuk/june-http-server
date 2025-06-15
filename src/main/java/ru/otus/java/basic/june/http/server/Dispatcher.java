package ru.otus.java.basic.june.http.server;

import ru.otus.java.basic.june.http.server.app.ItemsRepository;
import ru.otus.java.basic.june.http.server.exceptions_handling.BadRequestException;
import ru.otus.java.basic.june.http.server.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private Map<String, RequestProcessor> routes;
    private RequestProcessor defaultNotFountRequestProcessor;
    private RequestProcessor defaultStaticResourcesRequestProcessor;

    public Dispatcher() {
        this.routes = new HashMap<>();
        ItemsRepository itemsRepository = new ItemsRepository();
        this.routes.put("GET /hello", new HelloRequestProcessor());
        this.routes.put("GET /calc", new CalcRequestProcessor());
        this.routes.put("GET /items", new GetItemsRequestProcessor(itemsRepository));
        this.routes.put("POST /items", new CreateItemRequestProcessor(itemsRepository));
        this.defaultNotFountRequestProcessor = new DefaultNotFoundRequestProcessor();
        this.defaultStaticResourcesRequestProcessor = new DefaultStaticResourcesProcessor();
    }

    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (Files.exists(Paths.get("static/", request.getUri().substring(1)))) {
            defaultStaticResourcesRequestProcessor.execute(request, output);
            return;
        }
        if (!routes.containsKey(request.getRoutingKey())) {
            defaultNotFountRequestProcessor.execute(request, output);
            return;
        }
        try {
            routes.get(request.getRoutingKey()).execute(request, output);
        } catch (BadRequestException e) {
            String response = "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Type: text/html; charset=utf-8\r\n" +
                    "\r\n" +
                    "<html><body><h1>Bad Request</h1><p>" + e.getCode() + ": " + e.getDescription() + "</p></body></html>";
            output.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            String response = "HTTP/1.1 500 Internal Server Error\r\n" +
                    "Content-Type: text/html; charset=utf-8\r\n" +
                    "\r\n" +
                    "<html><body><h1>500 Internal Server Error</h1><h5>Ой, что-то сломалось, попробуйте позже...</h5></body></html>";
            output.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
