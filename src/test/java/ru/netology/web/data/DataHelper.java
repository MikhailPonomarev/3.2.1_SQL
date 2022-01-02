package ru.netology.web.data;

import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.*;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        String authCode = DataHelper.getAuthCode();
        return new VerificationCode(authCode);
    }

    //метод получения кода аутентификации из БД
    @SneakyThrows
    public static String getAuthCode() {
        String codeSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1;";
        String code = null;

        try (
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
                PreparedStatement preparedStatement = connection.prepareStatement(codeSQL);
        ) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    code = resultSet.getString(1);
                }
            }
        }
        return code;
    }


    //методы извлечения баланса карт
    @SneakyThrows
    public static int getFirstCardBalance() {
        String firstCardSQL = "SELECT balance_in_kopecks FROM cards WHERE number = '5559 0000 0000 0001';";

        int balance = 0;
        try (
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
                PreparedStatement preparedStatement = connection.prepareStatement(firstCardSQL);
        ) {
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    balance = resultSet.getInt(1);
                }
            }
        }
        return balance / 100;
    }

    @SneakyThrows
    public static int getSecondCardBalance() {
        String firstCardSQL = "SELECT balance_in_kopecks FROM cards WHERE number = '5559 0000 0000 0002';";

        int balance = 0;
        try (
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
                PreparedStatement preparedStatement = connection.prepareStatement(firstCardSQL);
        ) {
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    balance = resultSet.getInt(1);
                }
            }
        }
        return balance / 100;
    }

    public static String getFirstCard() {
        return "5559 0000 0000 0001";
    }

    public static String getSecondCard() {
        return "5559 0000 0000 0002";
    }

    public static String getFalseCard() {
        return "5559 0000 0000 0003";
    }
}