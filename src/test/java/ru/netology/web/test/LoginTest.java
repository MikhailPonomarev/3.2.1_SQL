package ru.netology.web.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class LoginTest {

    @Test
    public void shouldLoginWithAuthCodeFromDB() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @Test
    public void falseLoginThrice() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var falseAuthInfo = DataHelper.getFalseAuthInfo();
        loginPage.falseLogin(falseAuthInfo);
        loginPage.clearFields();
        loginPage.falseLogin(falseAuthInfo);
        loginPage.clearFields();
        loginPage.falseLogin(falseAuthInfo);

        String userStatus = DataHelper.getUserStatus();

        Assertions.assertEquals("blocked", userStatus);
    }
}
