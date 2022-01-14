package ru.netology.web.test;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import ru.netology.web.data.ApiHelper;
import ru.netology.web.data.DataHelper;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ApiTest {

    @BeforeAll
    static void validLoginAndAuth() {
        ApiHelper.validLogin();
    }


    @Test
    public void viewCardsData() {
        String token = ApiHelper.authAndGetValidToken();

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
        String token = ApiHelper.authAndGetValidToken();

        int amount = 2000;
        String transferRequestBody = ApiHelper
                .getTransferRequestBody(DataHelper.getFirstCard(), DataHelper.getSecondCard(), amount);
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
        String token = ApiHelper.authAndGetValidToken();

        int amount = 2000;
        String transferRequestBody = ApiHelper
                .getTransferRequestBody(DataHelper.getSecondCard(), DataHelper.getFirstCard(), amount);
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
        String token = ApiHelper.authAndGetValidToken();

        int firstCardBalance = DataHelper.getFirstCardBalance();
        int secondCardBalance = DataHelper.getSecondCardBalance();
        int amount = firstCardBalance + 1;
        String transferRequestBody = ApiHelper
                .getTransferRequestBody(DataHelper.getFirstCard(), DataHelper.getSecondCard(), amount);

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
        String token = ApiHelper.authAndGetValidToken();

        int amount = 2000;
        String transferRequestBody = ApiHelper
                .getTransferRequestBody(DataHelper.getFalseCard(), DataHelper.getFirstCard(), amount);
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
        String token = ApiHelper.authAndGetValidToken();

        int amount = 2000;
        String transferRequestBody = ApiHelper
                .getTransferRequestBody(DataHelper.getSecondCard(), DataHelper.getFalseCard(), amount);
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
                .spec(ApiHelper.getLoginSpec())
                .body(ApiHelper.falseLoginRequest())
                .when()
                .post()
                .then()
                .statusCode(400).extract().response().path("code").toString();

        assertEquals(response, "AUTH_INVALID");
    }

    @Test
    public void authWithFalseAuthCode() {
        String response = given()
                .spec(ApiHelper.getAuthSpec())
                .body(ApiHelper.getFalseAuthRequestBody())
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
        String token = ApiHelper.getFalseToken();

        int amount = 2000;
        String transferRequestBody = ApiHelper
                .getTransferRequestBody(DataHelper.getSecondCard(), DataHelper.getFirstCard(), amount);

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
