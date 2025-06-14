package ru.otus.java.basic.june.http.server;

import ru.otus.java.basic.june.http.server.app.ItemsRepository;
import ru.otus.java.basic.june.http.server.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private Map<String, RequestProcessor> routes;
    private RequestProcessor defaultNotFountRequestProcessor;

    public Dispatcher() {
        this.routes = new HashMap<>();
        ItemsRepository itemsRepository = new ItemsRepository();
        this.routes.put("GET /hello", new HelloRequestProcessor());
        this.routes.put("GET /calc", new CalcRequestProcessor());
        this.routes.put("GET /items", new GetItemsRequestProcessor(itemsRepository));
        this.routes.put("POST /items", new CreateItemRequestProcessor(itemsRepository));
        this.defaultNotFountRequestProcessor = new DefaultNotFoundRequestProcessor();
    }

    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (!routes.containsKey(request.getRoutingKey())) {
            defaultNotFountRequestProcessor.execute(request, output);
            return;
        }
        routes.get(request.getRoutingKey()).execute(request, output);
    }
}
