package ru.netology.dailyTrip.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.List;

public class DbHelper {
    private static QueryRunner runner;
    private static Connection conn;

    @SneakyThrows
    public static void setup() {
        runner = new QueryRunner();
        conn = DriverManager.getConnection(System.getProperty("dbUrl"), "app", "pass");
    }

    @SneakyThrows
    public static void setDown() {
        setup();
        var sqlUpdateOne = "DELETE FROM credit_request_entity;";
        var sqlUpdateTwo = "DELETE FROM payment_entity;";
        var sqlUpdateThree = "DELETE FROM order_entity;";
        runner.update(conn, sqlUpdateOne);
        runner.update(conn, sqlUpdateTwo);
        runner.update(conn, sqlUpdateThree);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentEntity {
        private String id;
        private int amount;
        private Timestamp created;
        private String status;
        private String transaction_id;
    }

    @SneakyThrows
    public static List<PaymentEntity> getPayments() {
        setup();
        var sqlQuery = "SELECT * FROM payment_entity ORDER BY created DESC;";
        ResultSetHandler<List<PaymentEntity>> resultHandler = new BeanListHandler<>(PaymentEntity.class);
        return runner.query(conn, sqlQuery, resultHandler);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditRequestEntity {
        private String id;
        private String bank_id;
        private Timestamp created;
        private String status;
    }

    @SneakyThrows
    public static List<CreditRequestEntity> getCreditsRequest() {
        setup();
        var sqlQuery = "SELECT * FROM credit_request_entity ORDER BY created DESC;";
        ResultSetHandler<List<CreditRequestEntity>> resultHandler = new BeanListHandler<>(CreditRequestEntity.class);
        return runner.query(conn, sqlQuery, resultHandler);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderEntity {
        private String id;
        private Timestamp created;
        private String credit_id;
        private String payment_id;
    }

    @SneakyThrows
    public static List<OrderEntity> getOrders() {
        setup();
        var sqlQuery = "SELECT * FROM order_entity ORDER BY created DESC;";
        ResultSetHandler<List<OrderEntity>> resultHandler = new BeanListHandler<>(OrderEntity.class);
        return runner.query(conn, sqlQuery, resultHandler);
    }
}
