package ru.netology.web.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.Assertions;

import java.sql.*;

import static io.restassured.RestAssured.given;

public class ApiHelper {
    private ApiHelper() {
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

    //метод логина с помощью API
    public static void loginByApi() {
        RequestSpecification loginSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/api/auth")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        String loginRequestBody = "{ 'login': 'vasya', 'password': 'qwerty123' }";

        Response loginResponse = given()
                .spec(loginSpec)
                .body(loginRequestBody)
                .when()
                .post()
                .then().extract().response();
        Assertions.assertEquals(200, loginResponse.statusCode());
    }

    //метод аутентификации с помощью API
    public static String authByApi() {
        RequestSpecification authSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("api/auth/verification")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        String authCode = DataHelper.getAuthCode();

        //тело auth запроса
        String authRequestBody = "{ 'login': 'vasya', 'code':" + " '" + authCode + "' }";

        //извлечения токена из ответа
        String token = given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .and().extract().path("token").toString();
        return token;
    }

}
