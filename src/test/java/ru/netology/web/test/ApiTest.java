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
    private static RequestSpecification loginSpec = ApiHelper.getLoginSpec();
    private static String validLoginRequest = ApiHelper.validLoginRequest();
    private String falseLoginRequestBody = ApiHelper.falseLoginRequest();
    private String falseAuthRequestBody = ApiHelper.getFalseAuthRequestBody();
    private static RequestSpecification authSpec = ApiHelper.getAuthSpec();
    private String token = ApiHelper.getToken();
    private String firstCard = DataHelper.getFirstCard();
    private String secondCard = DataHelper.getSecondCard();
    private String falseCard = DataHelper.getFalseCard();


    @BeforeAll
    static void validLoginAndAuth() {
        given()
                .spec(loginSpec)
                .body(validLoginRequest)
                .when()
                .post()
                .then()
                .statusCode(200);

        String authCode = DataHelper.getAuthCode();
        String authRequestBody = ApiHelper.getValidAuthRequestBody(authCode);

        given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200);
    }


    @Test
    public void viewCardsData() {
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
    }

    @Test
    public void moneyTransferFromFirstCardToSecond() {
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

        assertEquals(response, "AUTH_INVALID");
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
