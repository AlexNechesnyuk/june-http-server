package ru.otus.java.basic.june.http.server.processors;

import com.google.gson.Gson;
import ru.otus.java.basic.june.http.server.HttpRequest;
import ru.otus.java.basic.june.http.server.app.Item;
import ru.otus.java.basic.june.http.server.app.ItemsRepo;
import ru.otus.java.basic.june.http.server.app.ItemsRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CreateItemRequestProcessor implements RequestProcessor {
    private ItemsRepo itemsRepository;

    public CreateItemRequestProcessor(ItemsRepo itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        Gson gson = new Gson();
        Item item = gson.fromJson(request.getBody(), Item.class);
        item = itemsRepository.create(item);
        String itemJson = gson.toJson(item);
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                itemJson;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
