package ru.otus.java.basic.june.http.server.processors;

import com.google.gson.Gson;
import ru.otus.java.basic.june.http.server.HttpRequest;
import ru.otus.java.basic.june.http.server.app.Item;
import ru.otus.java.basic.june.http.server.app.ItemsRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetItemsRequestProcessor implements RequestProcessor {
    private ItemsRepository itemsRepository;

    public GetItemsRequestProcessor(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        List<Item> items = itemsRepository.getAll();
        Gson gson = new Gson();
        String itemJson = gson.toJson(items);

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                itemJson;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
