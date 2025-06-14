package ru.otus.java.basic.june.http.server;

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

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port + ". Ожидаем подключения");
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
                 OutputStream outputStream = socket.getOutputStream();) {
                byte[] buffer = new byte[8192];
                int n = socket.getInputStream().read(buffer);
                if (n < 1) {
                    return;
                }
                String rawRequest = new String(buffer, 0, n);
                HttpRequest request = new HttpRequest(rawRequest);
                request.info(true);
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
