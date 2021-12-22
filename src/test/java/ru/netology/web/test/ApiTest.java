package ru.netology.web.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.netology.web.data.ApiHelper;
import ru.netology.web.data.DataHelper;

import static io.restassured.RestAssured.given;

public class ApiTest {
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

//    private String authRequestBody = "{\n" +
//            "  \"login\": \"vasya\",\n" +
//            "  \"code\": \"\"\n" +
//            "}";
//


    @BeforeAll
    public static void deleteSqlData() {
        ApiHelper.clearTables();
    }

    @Test
    public void test() {
        ApiHelper.loginByApi();

        //auth_code из БД
        String authCode = DataHelper.getAuthCode();

        //тело auth запроса
        String authRequestBody = "{ 'login': 'vasya', 'code':" + " '" + authCode + "' }";

        //аутентификация
        Response authResponse = given()
                .spec(authSpec)
                .body(authRequestBody)
                .when()
                .post()
                .then().extract().response();
        String smth = authResponse.toString();
        System.out.println(smth);
    }
}
