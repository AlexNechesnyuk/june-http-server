package ru.otus.java.basic.june.http.server.processors;

import com.google.gson.Gson;
import ru.otus.java.basic.june.http.server.HttpRequest;
import ru.otus.java.basic.june.http.server.HttpServer;
import ru.otus.java.basic.june.http.server.app.ItemsRepo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GetItemsRequestProcessor implements RequestProcessor {
    private ItemsRepo itemsRepository;

    public GetItemsRequestProcessor(ItemsRepo itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        Gson gson = new Gson();
        String itemJson;
        try {
            long id = Integer.parseInt(request.getParameter("id"));
            itemJson = gson.toJson(itemsRepository.get(id));
        } catch (Exception e) {
            itemJson = gson.toJson(itemsRepository.getAll());
        }
        String response = HttpServer.jsonResponseBuild(itemJson);
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
