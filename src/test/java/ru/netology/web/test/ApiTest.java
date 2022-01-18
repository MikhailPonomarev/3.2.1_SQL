package ru.netology.web.test;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.web.data.ApiHelper.*;
import static ru.netology.web.data.DataHelper.*;

public class ApiTest {
    @BeforeAll
    static void validLogin() {
        int response = validLoginRequest(validLoginBody());
        assertEquals(200, response);
    }


    @Test
    public void viewCardsData() {
        String token = authAndGetToken(validAuthRequestBody(getAuthCode()));
        int response = viewCardsRequest(token);
        assertEquals(200, response);
    }

    @Test
    public void moneyTransferFromFirstCardToSecond() {
        String token = authAndGetToken(validAuthRequestBody(getAuthCode()));
        int amount = 2000;
        String transferRequestBody = getTransferRequestBody(getFirstCard(), getSecondCard(), amount);
        int firstCardBalance = getFirstCardBalance();
        int secondCardBalance = getSecondCardBalance();

        int response = moneyTransferRequest(transferRequestBody, token);
        assertEquals(200, response);

        int firstBalanceAfterTransfer = getFirstCardBalance();
        int secondBalanceAfterTransfer = getSecondCardBalance();
        assertEquals((secondCardBalance + amount), secondBalanceAfterTransfer);
        assertEquals((firstCardBalance - amount), firstBalanceAfterTransfer);
    }

    @Test
    public void moneyTransferFromSecondCardToFirst() {
        String token = authAndGetToken(validAuthRequestBody(getAuthCode()));
        int amount = 2000;
        String transferRequestBody = getTransferRequestBody(getSecondCard(), getFirstCard(), amount);
        int firstCardBalance = getFirstCardBalance();
        int secondCardBalance = getSecondCardBalance();

        int response = moneyTransferRequest(transferRequestBody, token);
        assertEquals(200, response);

        int firstBalanceAfterTransfer = getFirstCardBalance();
        int secondBalanceAfterTransfer = getSecondCardBalance();
        assertEquals((firstCardBalance + amount), firstBalanceAfterTransfer);
        assertEquals((secondCardBalance - amount), secondBalanceAfterTransfer);
    }

    @Test
    public void transferOverLimit() {
        String token = authAndGetToken(validAuthRequestBody(getAuthCode()));
        int firstCardBalance = getFirstCardBalance();
        int secondCardBalance = getSecondCardBalance();
        int amount = firstCardBalance + 1;
        String transferRequestBody = getTransferRequestBody(getFirstCard(), getSecondCard(), amount);

        int response = moneyTransferRequest(transferRequestBody, token);
        assertEquals(200, response);

        int firstBalanceAfterTransfer = getFirstCardBalance();
        int secondBalanceAfterTransfer = getSecondCardBalance();
        assertEquals((secondCardBalance + amount), secondBalanceAfterTransfer);
        assertTrue(firstBalanceAfterTransfer >= 0);
    }

    @Test
    public void transferFromNotExistingCard() {
        String token = authAndGetToken(validAuthRequestBody(getAuthCode()));
        int amount = 2000;
        String transferRequestBody = getTransferRequestBody(getFalseCard(), getFirstCard(), amount);
        int firstCardBalance = getFirstCardBalance();

        int response = moneyTransferRequest(transferRequestBody, token);
        assertEquals(400, response);

        int firstBalanceAfterTransfer = getFirstCardBalance();
        assertEquals(firstCardBalance, firstBalanceAfterTransfer);
    }

    @Test
    public void transferToNotExistingCard() {
        String token = authAndGetToken(validAuthRequestBody(getAuthCode()));
        int amount = 2000;
        String transferRequestBody = getTransferRequestBody(getSecondCard(), getFalseCard(), amount);
        int secondCardBalance = getSecondCardBalance();

        int response = moneyTransferRequest(transferRequestBody, token);
        assertEquals(400, response);

        int secondCardAfterTransfer = getSecondCardBalance();
        assertEquals(secondCardBalance, secondCardAfterTransfer);
    }

    @Test
    public void loginWithWrongPassword() {
        String response = falseLoginRequest(falseLoginBody());
        assertEquals("AUTH_INVALID", response);
    }

    @Test
    public void authWithFalseAuthCode() {
        String response = falseAuth(falseAuthRequestBody());
        assertEquals(response, "AUTH_INVALID");
    }

    @Test
    public void viewCardsWithWrongToken() {
        String token = getFalseToken();
        int response = falseViewCardsRequest(token);
        assertEquals(401, response);
    }

    @Test
    public void transferWithWrongToken() {
        String token = getFalseToken();
        int amount = 2000;
        String transferRequestBody = getTransferRequestBody(getSecondCard(), getFirstCard(), amount);

        int response = moneyTransferRequest(transferRequestBody, token);
        assertEquals(401, response);
    }
}
