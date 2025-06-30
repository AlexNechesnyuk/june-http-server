package ru.otus.java.basic.june.http.server.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.june.http.server.Settings;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemsRepositoryDB implements ItemsRepo {
    private List<Item> items;
    private static final String DATABASE_URL = Settings.getStringSettings("DATABASE_URL");
    private static final String DATABASE_USER = Settings.getStringSettings("DATABASE_USER");
    private static final String DATABASE_PASSWORD = Settings.getStringSettings("DATABASE_PASSWORD");
    private final Connection connection;
    private static final Logger logger = LogManager.getLogger(ItemsRepositoryDB.class);

    public ItemsRepositoryDB() throws SQLException {
        connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        if (!tableExists("item_info") || !tableExists("stock"))
            bdCreate();
    }

    @Override
    public List<Item> getAll() {
        String query = """
                    SELECT ii.id, ii.title, ii.price, COALESCE(s.stock, 0) AS stock
                    FROM item_info ii
                    LEFT JOIN stock s ON ii.id = s.item_id
                    ORDER BY ii.id
                """;

        List<Item> items = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                BigDecimal price = rs.getBigDecimal("price");
                long stock = rs.getInt("stock");
                Item item = new Item(id, title, price);
                item.setStock(stock);
                items.add(item);
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return items;
    }

    @Override
    public Item get(Long id) {
        String query = """
                    SELECT ii.id, ii.title, ii.price, COALESCE(s.stock, 0) AS stock
                    FROM item_info ii
                    LEFT JOIN stock s ON ii.id = s.item_id
                    WHERE ii.id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id_ = rs.getLong("id");
                    String title = rs.getString("title");
                    BigDecimal price = rs.getBigDecimal("price");
                    long stock = rs.getInt("stock");
                    Item item = new Item(id_, title, price);
                    item.setStock(stock);
                    return item;
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public Item create(Item item) {
        final String QUERY_UPSERT_ITEM = """
                INSERT INTO item_info (title, price)
                VALUES (?, ?)
                ON CONFLICT (title) DO UPDATE 
                    SET price = EXCLUDED.price
                RETURNING id;
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                QUERY_UPSERT_ITEM,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, item.getTitle());
            pstmt.setBigDecimal(2, item.getPrice());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long itemId = rs.getLong(1);
                        item.setId(itemId);
                        String checkStockSql = "SELECT COUNT(*) FROM stock WHERE item_id = ?";
                        try (PreparedStatement checkStmt = connection.prepareStatement(checkStockSql)) {
                            checkStmt.setLong(1, itemId);
                            try (ResultSet countRs = checkStmt.executeQuery()) {
                                if (countRs.next() && countRs.getInt(1) == 0) {
                                    String insertStockSql = "INSERT INTO stock (item_id, stock) VALUES (?, 0)";
                                    try (PreparedStatement stockStmt = connection.prepareStatement(insertStockSql)) {
                                        stockStmt.setLong(1, itemId);
                                        stockStmt.executeUpdate();
                                    }
                                }
                            }
                        }
                        logger.debug("Обработан товар '" + item.getTitle() +
                                "' | Цена: " + item.getPrice() +
                                " | ID: " + itemId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return item;
    }

    private boolean tableExists(String tableName) throws SQLException {
        String TEST_REQUEST = """
                SELECT EXISTS (
                SELECT 1 FROM information_schema.tables
                WHERE table_schema = 'public' AND table_name = ?
                )""";

        try (PreparedStatement stmt = connection.prepareStatement(TEST_REQUEST)) {
            stmt.setString(1, tableName.toLowerCase());
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
                    item_id int NOT NULL REFERENCES item_info(id) ON DELETE CASCADE,
                    stock int NOT null CHECK (stock >= 0)
                );
                """,
                "ALTER TABLE item_info ADD CONSTRAINT title_unique UNIQUE (title)"
        };
        final String QUERY_INIT_EXAMPLE_DATA = """
                WITH inserted_items AS (
                    INSERT INTO item_info (title, price)
                    VALUES
                        ('Milk', 80.00),
                        ('Bread', 38.00)
                    RETURNING id
                )
                INSERT INTO stock (item_id, stock)
                SELECT id, 3 FROM inserted_items;
                """;
        try (Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            for (String query : QUERY_CREATE_TABLE) {
                stmt.executeUpdate(query);
                logger.debug("Таблица успешно создана/обновлена");
            }
            stmt.executeUpdate(QUERY_INIT_EXAMPLE_DATA);
            logger.debug("Таблица заполнена начальными данными");
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM item_info WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
