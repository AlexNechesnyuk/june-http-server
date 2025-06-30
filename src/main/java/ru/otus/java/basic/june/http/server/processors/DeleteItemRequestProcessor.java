package ru.otus.java.basic.june.http.server.processors;

import com.google.gson.Gson;
import ru.otus.java.basic.june.http.server.HttpRequest;
import ru.otus.java.basic.june.http.server.HttpServer;
import ru.otus.java.basic.june.http.server.app.ItemsRepo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DeleteItemRequestProcessor implements RequestProcessor {
    private ItemsRepo itemsRepository;

    public DeleteItemRequestProcessor(ItemsRepo itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        long id = Integer.parseInt(request.getParameter("id"));
        Gson gson = new Gson();
        itemsRepository.delete(id);

        String itemJson = gson.toJson(itemsRepository.getAll());
        String response = HttpServer.jsonResponseBuild(itemJson);
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
