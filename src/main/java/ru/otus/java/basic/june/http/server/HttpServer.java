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
    private Dispatcher dispatcher;
    private static final Logger logger = LogManager.getLogger(HttpServer.class);

    public HttpServer() {
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        int port = Settings.getIntSettings("port");
        int threads = Settings.getIntSettings("threadPullSize");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Сервер запущен на порту " + port + ". Ожидаем подключения");
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new ServerThread(dispatcher, socket));
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    static public String htmlResponseBuild(String header, String body) {
        return "HTTP/1.1 " + header + "\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "\r\n" +
                "<html><body>" + body + "</body></html>";
    }

    static public String jsonResponseBuild(String itemJson) {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" + itemJson;
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
            int maxRequestSize = Settings.getIntSettings("maxRequestSize");
            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream();
                 ByteArrayOutputStream requestData = new ByteArrayOutputStream();) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
                    requestData.write(buffer, 0, bytesRead);
                    if (bytesRead < 8192 || requestData.size() > maxRequestSize) {
                        break;
                    }
                }
                if (bytesRead < 1) {
                    return;
                }
                String rawRequest = new String(requestData.toByteArray(), 0, requestData.size());
                HttpRequest request = HttpRequest.parse(rawRequest);
                request.info();
                dispatcher.execute(request, socket.getOutputStream());
            } catch (IOException e) {
                logger.error(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }
}
