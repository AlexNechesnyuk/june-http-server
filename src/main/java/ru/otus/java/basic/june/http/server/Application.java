package ru.otus.java.basic.june.http.server;

public class Application {
    // Дополнительная часть ДЗ:
    // - * Если в гет запросе продуктов есть параметр id, то возвращать только
    // один продукт с соответствующим id, если этого параметра нет, то возвращать
    // все продукты

    public static void main(String[] args) {
        new HttpServer(8189).start();
    }
}
