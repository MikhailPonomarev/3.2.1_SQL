package ru.netology.web.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class ApiTest {
    private RequestSpecification loginSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setBasePath("/api/auth")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private RequestSpecification authSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setBasePath("api/auth/verification")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private String loginRequestBody = "{\n" +
            "  \"login\": \"vasya\",\n" +
            "  \"password\": \"qwerty123\"\n" +
            "}";

    private String authRequestBody = "{\n" +
            "  \"login\": \"vasya\",\n" +
            "  \"code\": \"\"\n" +
            "}";



    @BeforeEach
    public void setUp() {
        Response response = given()
                .spec(loginSpec)
                .body(loginRequestBody)
                .when()
                .post()
                .then().extract().response();
        Assertions.assertEquals(200, response.statusCode());

    }


    @Test
    public void shouldVerifyByAuthCode() {
        Response response = given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then().extract().response();
        Assertions.assertEquals(200, response.statusCode());
        System.out.println(response);
    }
}
