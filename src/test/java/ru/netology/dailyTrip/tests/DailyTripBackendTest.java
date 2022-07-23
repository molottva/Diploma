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
    public void shouldApprovedHappyPathPay() {
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
    public void shouldDeclinedSadPathPay() {
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
    public void shouldApprovedHappyPathCredit() {
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
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Feature("Покупка тура в кредит")
    @Story("SadPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldDeclinedSadPathCredit() {
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
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Feature("Покупка тура по карте")
    @Story("Пустое body запроса")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldStatus400EmptyBodyPay() {
        user = DataHelper.getValidUserWithApprovedCard();
        given().spec(spec)
                .when().post(paymentUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура в кредит")
    @Story("Пустое body запроса")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldStatus400EmptyBodyCredit() {
        user = DataHelper.getValidUserWithApprovedCard();
        given().spec(spec)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура по карте")
    @Story("Пустое значение у атрибута number в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyNumberPay() {
        user = new DataHelper.UserData(null, DataHelper.generateMonth(1), DataHelper.generateYear(2),
                DataHelper.generateHolder(), DataHelper.generateCVC(3));
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура в кредит")
    @Story("Пустое значение у атрибута number в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyNumberCredit() {
        user = new DataHelper.UserData(null, DataHelper.generateMonth(1), DataHelper.generateYear(2),
                DataHelper.generateHolder(), DataHelper.generateCVC(3));
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура по карте")
    @Story("Пустое значение у атрибута month в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyMonthPay() {
        user = new DataHelper.UserData(DataHelper.getNumberByStatus("approved"), null, DataHelper.generateYear(2),
                DataHelper.generateHolder(), DataHelper.generateCVC(3));
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура в кредит")
    @Story("Пустое значение у атрибута month в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyMonthCredit() {
        user = new DataHelper.UserData(DataHelper.getNumberByStatus("approved"), null, DataHelper.generateYear(2),
                DataHelper.generateHolder(), DataHelper.generateCVC(3));
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура по карте")
    @Story("Пустое значение у атрибута year в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyYearPay() {
        user = new DataHelper.UserData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1), null,
                DataHelper.generateHolder(), DataHelper.generateCVC(3));
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура в кредит")
    @Story("Пустое значение у атрибута year в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyYearCredit() {
        user = new DataHelper.UserData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1), null,
                DataHelper.generateHolder(), DataHelper.generateCVC(3));
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура по карте")
    @Story("Пустое значение у атрибута holder в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyHolderPay() {
        user = new DataHelper.UserData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), null, DataHelper.generateCVC(3));
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура в кредит")
    @Story("Пустое значение у атрибута holder в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyHolderCredit() {
        user = new DataHelper.UserData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), null, DataHelper.generateCVC(3));
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура по карте")
    @Story("Пустое значение у атрибута cvc в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyCvcPay() {
        user = new DataHelper.UserData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), DataHelper.generateHolder(), null);
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Feature("Покупка тура в кредит")
    @Story("Пустое значение у атрибута cvc в body запроса")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldStatus400EmptyCvcCredit() {
        user = new DataHelper.UserData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), DataHelper.generateHolder(), null);
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }
}
