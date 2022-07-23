package ru.netology.dailyTrip.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.*;
import ru.netology.dailyTrip.helpers.DataHelper;
import ru.netology.dailyTrip.helpers.DbHelper;
import ru.netology.dailyTrip.page.DailyTripPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.testng.AssertJUnit.*;

@Epic("Frontend тестирование функционала Путешествие дня")
public class DailyTripFrontendTest {
    private static DataHelper.UserData user;
    private static DailyTripPage dailyTrip;
    private static List<DbHelper.PaymentEntity> payments;
    private static List<DbHelper.CreditRequestEntity> credits;
    private static List<DbHelper.OrderEntity> orders;

    @BeforeClass
    public void setupClass() {
        DbHelper.setDown();
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true).savePageSource(true));
    }

    @BeforeMethod
    public void setupMethod() {
        open("http://localhost:8080/");
        dailyTrip = new DailyTripPage();
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
        dailyTrip.clickPayButton();
        dailyTrip.insert(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.matchesInputValue(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.success();

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());

        assertEquals(dailyTrip.getAmount() * 100, payments.get(0).getAmount());
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
        dailyTrip.clickPayButton();
        dailyTrip.insert(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.matchesInputValue(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.error();

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());

        assertEquals(dailyTrip.getAmount() * 100, payments.get(0).getAmount());
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
        dailyTrip.clickCreditButton();
        dailyTrip.insert(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.matchesInputValue(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.success();

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
    public void shouldSadPathCredit() {
        user = DataHelper.getValidUserWithDeclinedCard();
        dailyTrip.clickCreditButton();
        dailyTrip.insert(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.matchesInputValue(user.getNumber(), user.getMonth(), user.getYear(), user.getHolder(), user.getCvc());
        dailyTrip.error();

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
}
