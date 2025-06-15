package ru.otus.java.basic.june.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;
    private static final Logger logger = LogManager.getLogger(HttpServer.class);

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Сервер запущен на порту " + port + ". Ожидаем подключения");
            ExecutorService executor = Executors.newFixedThreadPool(3);
            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new ServerThread(dispatcher, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ServerThread implements Runnable {
        private final Socket socket;
        private final Dispatcher dispatcher;

        ServerThread(Dispatcher dispatcher, Socket socket) {
            this.socket = socket;
            this.dispatcher = dispatcher;
        }

        @Override
        public void run() {
            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream();
                 ByteArrayOutputStream requestData = new ByteArrayOutputStream();) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
                    requestData.write(buffer, 0, bytesRead);
                    if (bytesRead < 8192 || requestData.size() > 256 * 1024) {
                        break;
                    }
                }
                if (bytesRead < 1) {
                    return;
                }
                String rawRequest = new String(requestData.toByteArray(), 0, requestData.size());
                HttpRequest request = new HttpRequest(rawRequest);
                request.info();
                dispatcher.execute(request, socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
