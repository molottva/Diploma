package ru.netology.dailyTrip.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.gson.Gson;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.netology.dailyTrip.helpers.DataHelper;
import ru.netology.dailyTrip.helpers.DbHelper;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.*;

//todo проверка с БД

@Epic("Backend тестирование функционала Путешествие дня")
public class DailyTripBackendTest {
    private static DataHelper.UserData user;
    private static Gson gson = new Gson();
    private static RequestSpecification spec = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(9999)
            .setAccept(ContentType.JSON).setContentType(ContentType.JSON).log(LogDetail.ALL).build();
    private static String paymentUrl = "/payment";
    private static String creditUrl = "/credit";
    private static List<DbHelper.PaymentEntity> payments;
    private static List<DbHelper.CreditRequestEntity> credits;
    private static List<DbHelper.OrderEntity> orders;

    @BeforeClass
    public void setupClass() {
        DbHelper.setDown();
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true).savePageSource(true));
    }

    @AfterMethod
    public void setDownMethod() {
        DbHelper.setDown();
    }

    @AfterClass
    public void setDownClass() {
        SelenideLogger.removeListener("allure");
    }

    @Feature("Покупка тура по карте")
    @Story("HappyPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldHappyPathPay() {
        user = DataHelper.getValidUserWithApprovedCard();
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(200);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());

        assertTrue(payments.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        assertNull(orders.get(0).getCredit_id());
    }

    @Feature("Покупка тура по карте")
    @Story("SadPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldSadPathPay() {
        user = DataHelper.getValidUserWithDeclinedCard();
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(200);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());

        assertTrue(payments.get(0).getStatus().equalsIgnoreCase("declined"));
        assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        assertNull(orders.get(0).getCredit_id());
    }

    @Feature("Покупка тура в кредит")
    @Story("HappyPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldHappyPathCredit() {
        user = DataHelper.getValidUserWithApprovedCard();
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId() ,orders.get(0).getCredit_id());
    }

    @Feature("Покупка тура в кредит")
    @Story("SadPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldSadPathCredit() {
        user = DataHelper.getValidUserWithDeclinedCard();
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("declined"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId() ,orders.get(0).getCredit_id());
    }
}
