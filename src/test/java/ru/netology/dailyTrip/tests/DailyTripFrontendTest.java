package ru.netology.dailyTrip.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.netology.dailyTrip.helpers.DataHelper;
import ru.netology.dailyTrip.helpers.DbHelper;
import ru.netology.dailyTrip.page.DailyTripPage;

import static com.codeborne.selenide.Selenide.open;

//todo проверка с бд

@Epic("Frontend тестирование функционала Путешествие дня")
public class DailyTripFrontendTest {
    private static DataHelper.UserData user;
    private static DailyTripPage dailyTrip;

    @BeforeClass
    public void setupClass() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true).savePageSource(true));
    }

    @BeforeMethod
    public void setupMethod() {
        open("http://localhost:8080/");
        dailyTrip = new DailyTripPage();
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
        dailyTrip.clickPayButton();
        dailyTrip.insert(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.matchesInputValue(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.success();
    }

    @Feature("Покупка тура по карте")
    @Story("SadPath")
    @Test
    public void shouldSadPathPay() {
        user = DataHelper.getValidUserWithDeclinedCard();
        dailyTrip.clickPayButton();
        dailyTrip.insert(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.matchesInputValue(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.error();
    }

    @Feature("Покупка тура в кредит")
    @Story("HappyPath")
    @Test
    public void shouldHappyPathCredit() {
        user = DataHelper.getValidUserWithApprovedCard();
        dailyTrip.clickCreditButton();
        dailyTrip.insert(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.matchesInputValue(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.success();
    }

    @Feature("Покупка тура в кредит")
    @Story("SadPath")
    @Test
    public void shouldSadPathCredit() {
        user = DataHelper.getValidUserWithDeclinedCard();
        dailyTrip.clickCreditButton();
        dailyTrip.insert(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.matchesInputValue(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.error();
    }
}
