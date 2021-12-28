package ru.netology.web.test;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.netology.web.data.ApiHelper;
import ru.netology.web.data.DataHelper;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ApiTest {
    private RequestSpecification loginSpec = ApiHelper.getLoginSpec();
    private String loginRequestBody = ApiHelper.validLoginRequest();
    private String falseLoginRequestBody = ApiHelper.falseLoginRequest();
    private String falseAuthRequestBody = ApiHelper.getFalseAuthRequestBody();
    private RequestSpecification authSpec = ApiHelper.getAuthSpec();
    private String firstCard = DataHelper.getFirstCard();
    private String secondCard = DataHelper.getSecondCard();
    private String falseCard = DataHelper.getFalseCard();

    @BeforeAll
    public static void deleteSqlData() {
        DataHelper.clearTables();
    }

    @Test
    public void moneyTransferFromFirstCardToSecond() {
        //логин
        given()
                .spec(loginSpec)
                .body(loginRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200);

        //аутентификация
        String authCode = DataHelper.getAuthCode();
        String authRequestBody = ApiHelper.getValidAuthRequestBody(authCode);

        String token = given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .and().extract().path("token").toString();

        //просмотр данных карт
        given()
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
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("cards.schema.json"));

        //перевод с карты на карту
        int amount = 2000;
        String transferRequestBody = ApiHelper.getTransferRequestBody(firstCard, secondCard, amount);
        int firstCardBalance = DataHelper.getFirstCardBalance();
        int secondCardBalance = DataHelper.getSecondCardBalance();

        given()
                .body(transferRequestBody)
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
                .statusCode(200);

        int firstBalanceAfterTransfer = DataHelper.getFirstCardBalance();
        int secondBalanceAfterTransfer = DataHelper.getSecondCardBalance();

        assertEquals((secondCardBalance + amount), secondBalanceAfterTransfer);
        assertEquals((firstCardBalance - amount), firstBalanceAfterTransfer);
    }

    @Test
    public void moneyTransferFromSecondCardToFirst() {
        //логин
        given()
                .spec(loginSpec)
                .body(loginRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200);

        //аутентификация
        String authCode = DataHelper.getAuthCode();
        String authRequestBody = ApiHelper.getValidAuthRequestBody(authCode);

        String token = given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .and().extract().path("token").toString();

        //просмотр данных карт
        given()
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
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("cards.schema.json"));

        //перевод с карты на карту
        int amount = 2000;
        String transferRequestBody = ApiHelper.getTransferRequestBody(secondCard, firstCard, amount);
        int firstCardBalance = DataHelper.getFirstCardBalance();
        int secondCardBalance = DataHelper.getSecondCardBalance();

        given()
                .body(transferRequestBody)
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
                .statusCode(200);

        int firstBalanceAfterTransfer = DataHelper.getFirstCardBalance();
        int secondBalanceAfterTransfer = DataHelper.getSecondCardBalance();

        assertEquals((firstCardBalance + amount), firstBalanceAfterTransfer);
        assertEquals((secondCardBalance - amount), secondBalanceAfterTransfer);
    }

    @Test
    public void transferOverLimit() {
        //логин
        given()
                .spec(loginSpec)
                .body(loginRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200);

        //аутентификация
        String authCode = DataHelper.getAuthCode();
        String authRequestBody = ApiHelper.getValidAuthRequestBody(authCode);

        String token = given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .and().extract().path("token").toString();

        //просмотр данных карт
        given()
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
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("cards.schema.json"));

        //перевод с карты на карту
        int firstCardBalance = DataHelper.getFirstCardBalance();
        int secondCardBalance = DataHelper.getSecondCardBalance();
        int amount = firstCardBalance + 1;
        String transferRequestBody = ApiHelper.getTransferRequestBody(firstCard, secondCard, amount);

        given()
                .body(transferRequestBody)
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
                .statusCode(200);

        int firstBalanceAfterTransfer = DataHelper.getFirstCardBalance();
        int secondBalanceAfterTransfer = DataHelper.getSecondCardBalance();

        assertEquals((secondCardBalance + amount), secondBalanceAfterTransfer);
        assertTrue(firstBalanceAfterTransfer >= 0);
    }

    @Test
    public void transferFromNotExistingCard() {
        //логин
        given()
                .spec(loginSpec)
                .body(loginRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200);

        //аутентификация
        String authCode = DataHelper.getAuthCode();
        String authRequestBody = ApiHelper.getValidAuthRequestBody(authCode);

        String token = given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .and().extract().path("token").toString();

        //просмотр данных карт
        given()
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
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("cards.schema.json"));

        //перевод с карты на карту
        int amount = 2000;
        String transferRequestBody = ApiHelper.getTransferRequestBody(falseCard, firstCard, amount);
        int firstCardBalance = DataHelper.getFirstCardBalance();

        given()
                .body(transferRequestBody)
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
                .statusCode(400);

        int firstBalanceAfterTransfer = DataHelper.getFirstCardBalance();

        assertEquals(firstCardBalance, firstBalanceAfterTransfer);
    }

    @Test
    public void transferToNotExistingCard() {
        //логин
        given()
                .spec(loginSpec)
                .body(loginRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200);

        //аутентификация
        String authCode = DataHelper.getAuthCode();
        String authRequestBody = ApiHelper.getValidAuthRequestBody(authCode);

        String token = given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .and().extract().path("token").toString();

        //просмотр данных карт
        given()
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
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("cards.schema.json"));

        //перевод с карты на карту
        int amount = 2000;
        String transferRequestBody = ApiHelper.getTransferRequestBody(secondCard, falseCard, amount);
        int secondCardBalance = DataHelper.getSecondCardBalance();

        given()
                .body(transferRequestBody)
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
                .statusCode(400);

        int secondCardAfterTransfer = DataHelper.getSecondCardBalance();

        assertEquals(secondCardBalance, secondCardAfterTransfer);
    }

    @Test
    public void loginWithWrongPassword() {
        String response = given()
                .spec(loginSpec)
                .body(falseLoginRequestBody)
                .when()
                .post()
                .then()
                .statusCode(400).extract().response().path("code").toString();

        assertEquals(response, "AUTH_INVALID");
    }

    @Test
    public void authWithFalseAuthCode() {
        String response = given()
                .spec(authSpec)
                .body(falseAuthRequestBody)
                .when()
                .post()
                .then()
                .statusCode(400)
                .and().extract().path("code").toString();

        assertEquals(response, "SERVER_ERROR");
    }

    @Test
    public void viewCardsWithWrongToken() {
        String token = "token";

        given()
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
                .statusCode(401);
    }

    @Test
    public void transferWithWrongToken() {
        String token = "token";
        int amount = 2000;
        String transferRequestBody = ApiHelper.getTransferRequestBody(secondCard, firstCard, amount);

        given()
                .body(transferRequestBody)
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
                .statusCode(401);
    }
}
