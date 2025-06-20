package ru.otus.java.basic.june.http.server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {
    // Дополнительная часть ДЗ:
    // - Добавить логирование вместо sout'ов
    // - Добавить возможность получения запроса размером более 8 кб
    // - * Добавить в ответ 404 какую-нибудь картинку, связанную с чем-то ненайденным
    private static final Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("HTTP сервер запускается");
        new HttpServer(8189).start();
    }
}
