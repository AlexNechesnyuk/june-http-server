package ru.otus.java.basic.june.http.server.processors;

import ru.otus.java.basic.june.http.server.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultStaticResourcesProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest httpRequest, OutputStream output) throws IOException {
        String filename = httpRequest.getUri().substring(1);
        Path filePath = Paths.get("static/", filename);
        byte[] fileData;
        try {
            fileData = Files.readAllBytes(filePath);
        } catch (IOException e) {
            fileData = new byte[0];
        }
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + fileData.length + "\r\n" +
                "\r\n";
        output.write(response.getBytes());
        output.write(fileData);
    }
}
