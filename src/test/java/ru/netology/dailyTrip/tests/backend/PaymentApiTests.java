package ru.netology.dailyTrip.tests.backend;

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
@Feature("Покупка тура по карте")
public class PaymentApiTests {
    private static DataHelper.CardData cardData;
    private static final Gson gson = new Gson();
    private static final RequestSpecification spec = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(9999)
            .setAccept(ContentType.JSON).setContentType(ContentType.JSON).log(LogDetail.ALL).build();
    private static final String paymentUrl = "/payment";
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

    @Story("HappyPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldHappyPath() {
        cardData = DataHelper.getValidApprovedCard();
        var body = gson.toJson(cardData);
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

    @Story("SadPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldSadPath() {
        cardData = DataHelper.getValidDeclinedCard();
        var body = gson.toJson(cardData);
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

    @Story("Пустое body запроса")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyBody() {
        cardData = DataHelper.getValidApprovedCard();
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

    @Story("Пустое значение у атрибута number в body запроса")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyNumber() {
        cardData = new DataHelper.CardData(null, DataHelper.generateMonth(1), DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
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

    @Story("Пустое значение у атрибута month в body запроса")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyMonth() {
        cardData = new DataHelper.CardData(DataHelper.getNumberByStatus("approved"), null, DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
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

    @Story("Пустое значение у атрибута year в body запроса")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyYear() {
        cardData = new DataHelper.CardData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1), null,
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
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

    @Story("Пустое значение у атрибута holder в body запроса")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyHolder() {
        cardData = new DataHelper.CardData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), null, DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
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

    @Story("Пустое значение у атрибута cvc в body запроса")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyCvc() {
        cardData = new DataHelper.CardData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), DataHelper.generateValidHolder(), null);
        var body = gson.toJson(cardData);
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
}
