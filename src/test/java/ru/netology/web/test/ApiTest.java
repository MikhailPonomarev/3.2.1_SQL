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

    @BeforeAll
    public static void deleteSqlData() {
        ApiHelper.clearTables();
    }

    @Test
    public void test() {
        ApiHelper.loginByApi();
        ApiHelper.authByApi();
        System.out.println(ApiHelper.authByApi());
    }
}
