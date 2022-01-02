package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.conditions.Visible;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private SelenideElement loginField = $("[data-test-id=login] input");
    private SelenideElement passwordField = $("[data-test-id=password] input");
    private SelenideElement loginButton = $("[data-test-id=action-login]");
    private SelenideElement errorNotification = $("[data-test-id=error-notification]");
    private SelenideElement errorNotificationText = $(".notification__content");

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
        return new VerificationPage();
    }

    public void falseLogin(DataHelper.falseAuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
        errorNotification.shouldBe(Condition.visible);
        errorNotificationText.shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    public void clearFields() {
        loginField.doubleClick().sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);;
        passwordField.doubleClick().sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);;
    }
}