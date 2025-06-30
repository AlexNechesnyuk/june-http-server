package ru.otus.java.basic.june.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.june.http.server.app.ItemsRepositoryDB;
import ru.otus.java.basic.june.http.server.app.ItemsRepo;
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
    private Map<Long, RequestProcessor> routes;
    private RequestProcessor defaultNotFountRequestProcessor;
    private RequestProcessor defaultStaticResourcesRequestProcessor;
    private static final Logger logger = LogManager.getLogger(Dispatcher.class);

    public Dispatcher() {
        this.routes = new HashMap<>();
        try {
            ItemsRepo itemsRepository = new ItemsRepositoryDB();
            this.routes.put(HttpRequest.computeHashFromString("GET /hello"), new HelloRequestProcessor());
            this.routes.put(HttpRequest.computeHashFromString("GET /calc"), new CalcRequestProcessor());
            this.routes.put(HttpRequest.computeHashFromString("GET /items"), new GetItemsRequestProcessor(itemsRepository));
            this.routes.put(HttpRequest.computeHashFromString("POST /items"), new CreateItemRequestProcessor(itemsRepository));
            this.routes.put(HttpRequest.computeHashFromString("DELETE /items"), new DeleteItemRequestProcessor(itemsRepository));
            this.defaultNotFountRequestProcessor = new DefaultNotFoundRequestProcessor();
            this.defaultStaticResourcesRequestProcessor = new DefaultStaticResourcesProcessor();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (Files.exists(Paths.get("static/", request.getUri().substring(1)))) {
            defaultStaticResourcesRequestProcessor.execute(request, output);
            logger.debug("execute defaultStaticResourcesRequestProcessor");
            return;
        }
        logger.debug(request.getRoutingKey());
        if (!routes.containsKey(request.getRoutingKey())) {
            defaultNotFountRequestProcessor.execute(request, output);
            logger.debug("execute defaultNotFountRequestProcessor");
            return;
        }
        try {
            routes.get(request.getRoutingKey()).execute(request, output);
        } catch (BadRequestException e) {
            logger.error(e);
            String response = HttpServer.htmlResponseBuild("400 Bad Request", "<h1>Bad Request</h1><p>\" + e.getCode() + \": \" + e.getDescription() + \"</p>");
            output.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error(e);
            String response = HttpServer.htmlResponseBuild("500 Internal Server Error", "<h1>500 Internal Server Error</h1><h5>Ой, что-то сломалось, попробуйте позже...</h5>");
            output.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
