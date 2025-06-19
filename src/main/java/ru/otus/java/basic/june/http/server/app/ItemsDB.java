package ru.otus.java.basic.june.http.server.app;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemsDB implements ItemsRepo{
    private List<Item> items;
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/db";
    private static final String DATABASE_USER = "user";
    private static final String DATABASE_PASSWORD = "pass";
    private static final String USERS_QUERY = "select * from users;";
    private static final String USER_ROLES_QUERY = """
            select r.id, r."name" from  role r
            join usertorole utr on r.id = utr.roleid
            where  utr.userid = ?;
            """;
    private static final String USER_ROLE_BY_EMAIL_QUERY = """
            SELECT r.name AS role_name, r.isAdmin
            FROM users u
            JOIN usertorole ur ON u.id = ur.userid
            JOIN role r ON ur.roleid = r.id
            WHERE u.email = ?;
            """;
    private static final String IS_ADMIN_QUERY2 = """
            select count(1) from role r
            join usertorole utr on r.id = utr.roleid
            where  r.isAdmin = true and utr.userid = ?;
            """;
    private static final String IS_ADMIN_QUERY = """
            select count(1) from role r
            join usertorole utr on r.id = utr.roleid
            where  utr.userid = ? and r.name = 'admin';
            """;
    private final Connection connection;

    public ItemsDB() throws SQLException {
        System.out.println("ItemsDB");
        connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        if (!tableExists("item_info") || !tableExists("stock"))
            bdCreate();

        this.items = new ArrayList<>(Arrays.asList(
                new Item(1L, "Milk", BigDecimal.valueOf(80)),
                new Item(2L, "Bread", BigDecimal.valueOf(38))
        ));


    }

    @Override
    public List<Item> getAll() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public Item get(Long id) {
        for (Item i : items) {
            if (i.getId().equals(id)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public Item create(Item item) {
        Long newId = 1L;
        for (Item i : items) {
            if (newId <= i.getId()) {
                newId = i.getId() + 1L;
            }
        }
        item.setId(newId);
        items.add(item);
        return item;
    }

    private boolean tableExists(String tableName) throws SQLException {
        String TEST_REQUEST = """
                SELECT EXISTS (
                SELECT 1 FROM information_schema.tables
                WHERE table_schema = 'public' AND table_name = ?
                )""";

        try (PreparedStatement stmt = connection.prepareStatement(TEST_REQUEST)) {
            stmt.setString(1, tableName.toLowerCase()); // Имена таблиц в PostgreSQL обычно в нижнем регистре
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }



    private void bdCreate() throws SQLException {
        final String[] QUERY_CREATE_TABLE = {"""
                CREATE TABLE item_info(
                    id serial PRIMARY KEY,
                    title varchar(255) NOT null,
                    price numeric(10,2) NOT null CHECK (price >= 0)
                );
                """,

                """
                CREATE TABLE stock(
                    item_id int NOT NULL REFERENCES item_info(id),
                    stock int NOT null CHECK (stock >= 0)
                );
                """};
        try (Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            for (String query : QUERY_CREATE_TABLE) {
                stmt.executeUpdate(query);
                System.out.println("Таблица успешно создана/обновлена");
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }
}
