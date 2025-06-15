package ru.otus.java.basic.june.http.server;

public class Application {
    // Дополнительная часть ДЗ:
    // - Добавить логирование вместо sout'ов
    // - Добавить возможность получения запроса размером более 8 кб
    // - * Добавить в ответ 404 какую-нибудь картинку, связанную с чем-то ненайденным

    public static void main(String[] args) {
        new HttpServer(8189).start();
    }
}
