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
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        private String code;
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

    //метод очистки таблиц auth_codes и cards_transactions
    @SneakyThrows
    public static void clearTables() {
        QueryRunner runner = new QueryRunner();
        String deleteAuth = "TRUNCATE TABLE auth_codes;";
        String deleteCardTransactions = "TRUNCATE TABLE card_transactions;";

        try (
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            runner.execute(connection, deleteAuth);
            runner.execute(connection, deleteCardTransactions);
        }
    }
}