package ru.netology.web.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ApiHelper {
    //спецификация API логина
    public static RequestSpecification getLoginSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/api/auth")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    public static String validLoginBody() {
        return "{ 'login': 'vasya', 'password': 'qwerty123' }";
    }

    public static String falseLoginBody() {
        return "{ 'login': 'vasya', 'password': 'password' }";
    }

    //методы логина и аутентификации с извлечением токена
    public static int validLoginRequest(String requestBody) {
        return given()
                .spec(getLoginSpec())
                .body(requestBody)
                .when()
                .post("http://localhost:9999/api/auth")
                .then()
                .extract()
                .statusCode();
    }

    public static String falseLoginRequest(String requestBody) {
        return given()
                .spec(getLoginSpec())
                .body(requestBody)
                .when()
                .post("http://localhost:9999/api/auth")
                .then()
                .statusCode(400)
                .and()
                .extract()
                .path("code");
    }

    public static String validAuthRequestBody(String code) {
        return "{ 'login': 'vasya', 'code':" + " '" + code + "' }";
    }

    public static String falseAuthRequestBody() {
        return "{ 'login': 'vasya', 'code':" + " '" + "code" + "' }";
    }

    //спецификация API аутентификации
    public static RequestSpecification getAuthSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("api/auth/verification")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    public static String authAndGetToken(String requestBody) {
        return given()
                .spec(getAuthSpec())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .and().extract().path("token").toString();
    }

    public static String falseAuth(String requestBody) {
        return given()
                .spec(getAuthSpec())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(400)
                .and().extract().path("code").toString();
    }

    public static String getFalseToken() {
        return "token";
    }


    //метод просмотра данных карт
    public static int viewCardsRequest(String token) {
        return given()
                .headers(
                        "Authorization",
                        "Bearer " + token,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .get("http://localhost:9999/api/cards")
                .then()
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("cards.schema.json"))
                .extract()
                .statusCode();
    }

    public static int falseViewCardsRequest(String token) {
        return given()
                .headers(
                        "Authorization",
                        "Bearer " + token,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .get("http://localhost:9999/api/cards")
                .then()
                .extract()
                .statusCode();
    }


    //тело запроса денежного перевода
    public static String getTransferRequestBody(String fromCard, String toCard, int amount) {
        return "{'from': " + "'" + fromCard + "'" + ", " + "'to': " + "'" + toCard
                + "'" + ", " + "'amount': " + amount + "}";
    }

    //метод денежного перевода
    public static int moneyTransferRequest(String requestBody, String token) {
        return given()
                .body(requestBody)
                .and()
                .headers(
                        "Authorization",
                        "Bearer " + token,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .post("http://localhost:9999/api/transfer")
                .then()
                .extract()
                .statusCode();
    }
}
