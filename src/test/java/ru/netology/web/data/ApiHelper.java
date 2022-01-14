package ru.netology.web.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;

//TODO: реализовать добычу токена 1 раз перед выполнением тестов
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

    public static String validLoginRequest() {
        return "{ 'login': 'vasya', 'password': 'qwerty123' }";
    }

    public static String falseLoginRequest() {
        return "{ 'login': 'vasya', 'password': 'password' }";
    }


    //спецификация API аутентификации
    public static RequestSpecification getAuthSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/api/auth/verification")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    public static String getValidAuthRequestBody(String code) {
        return "{ 'login': 'vasya', 'code':" + " '" + code + "' }";
    }

    public static String getFalseAuthRequestBody() {
        return "{ 'login': 'vasya', 'code':" + " '" + "code" + "' }";
    }


    //тело запроса денежного перевода

    public static String getTransferRequestBody(String fromCard, String toCard, int amount) {
        return "{'from': " + "'" + fromCard + "'" + ", " + "'to': " + "'" + toCard
                + "'" + ", " + "'amount': " + amount + "}";
    }

    //методы валидного логина и аутентификации с извлечением токена
    public static void validLogin() {
        int statusCode = given()
                .spec(getLoginSpec())
                .body(validLoginRequest())
                .when()
                .post()
                .then()
                .extract().statusCode();

        Assertions.assertEquals(200, statusCode);
    }

    public static String authAndGetValidToken() {
        return given()
                .spec(getAuthSpec())
                .body(getValidAuthRequestBody(DataHelper.getAuthCode()))
                .when()
                .post()
                .then()
                .statusCode(200)
                .and().extract().path("token").toString();
    }

    public static String getFalseToken() {
        return "token";
    }
}
