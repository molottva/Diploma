package ru.netology.dailyTrip.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.gson.Gson;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.netology.dailyTrip.helpers.DataHelper;
import ru.netology.dailyTrip.helpers.DbHelper;

import static io.restassured.RestAssured.given;

//todo проверка с БД

@Epic("Backend тестирование функционала Путешествие дня")
public class DailyTripBackendTest {
    private static DataHelper.UserData user;
    private static Gson gson = new Gson();
    private static RequestSpecification spec = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(9999)
            .setAccept(ContentType.JSON).setContentType(ContentType.JSON).log(LogDetail.ALL).build();
    private static String paymentUrl = "/payment";
    private static String creditUrl = "/credit";

    @BeforeClass
    public void setupClass() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true).savePageSource(true));
    }

    @AfterClass
    public void setDownClass() {
        SelenideLogger.removeListener("allure");
        DbHelper.setDown();
    }

    @Feature("Покупка тура по карте")
    @Story("HappyPath")
    @Test
    public void shouldHappyPathPay() {
        user = DataHelper.getValidUserWithApprovedCard();
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(200);
    }

    @Feature("Покупка тура по карте")
    @Story("SadPath")
    @Test
    public void shouldSadPathPay() {
        user = DataHelper.getValidUserWithDeclinedCard();
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(paymentUrl)
                .then().statusCode(200);
    }

    @Feature("Покупка тура в кредит")
    @Story("HappyPath")
    @Test
    public void shouldHappyPathCredit() {
        user = DataHelper.getValidUserWithApprovedCard();
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);
    }

    @Feature("Покупка тура в кредит")
    @Story("SadPath")
    @Test
    public void shouldSadPathCredit() {
        user = DataHelper.getValidUserWithDeclinedCard();
        var body = gson.toJson(user);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);
    }
}
