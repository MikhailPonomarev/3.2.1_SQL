package ru.netology.web.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.netology.web.data.ApiHelper;
import ru.netology.web.data.DataHelper;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ApiTest {

    @BeforeAll
    public static void deleteSqlData() {
        DataHelper.clearTables();
    }

    @Test
    public void viewCardsResponseShouldMatchJsonSchema() {
        ApiHelper.loginByApi();
        String token = ApiHelper.authByApi();

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
    public void transactionUsingApi() {
        //TODO: написать тест на денежный перевод
    }
}
