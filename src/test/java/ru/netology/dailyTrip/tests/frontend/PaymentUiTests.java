package ru.netology.dailyTrip.tests.frontend;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.*;
import ru.netology.dailyTrip.helpers.DataHelper;
import ru.netology.dailyTrip.helpers.DbHelper;
import ru.netology.dailyTrip.pages.TripCardPage;
import ru.netology.dailyTrip.pages.TripFormPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.testng.AssertJUnit.*;

@Epic("Frontend тестирование функционала Путешествие дня")
@Feature("Покупка тура по карте")
public class PaymentUiTests {
    private static DataHelper.CardData cardData;
    private static TripCardPage tripCard;
    private static TripFormPage tripForm;
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
        tripCard = new TripCardPage();
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

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());

        assertEquals(tripCard.getAmount() * 100, payments.get(0).getAmount());
        assertTrue(payments.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        assertNull(orders.get(0).getCredit_id());
    }

    @Story("SadPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldSadPath() {
        cardData = DataHelper.getValidDeclinedCard();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationWithErrorNotification();

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());

        assertEquals(tripCard.getAmount() * 100, payments.get(0).getAmount());
        assertTrue(payments.get(0).getStatus().equalsIgnoreCase("declined"));
        assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        assertNull(orders.get(0).getCredit_id());
    }

    @Story("Переключение с формы кредита на форму покупки")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldImmutableInputValuesAfterClickButton() {
        cardData = DataHelper.getValidApprovedCard();

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripCard.clickPayButton();
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
    }

    @Story("Пустое поле номер карты")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyNumber() {
        cardData = DataHelper.getValidApprovedCard();
        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm("", cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue("", cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertNumberFieldIsEmptyValue();
    }

    @Story("Заполнение поля номера карты без пробелов")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldSuccessfulWithoutSpacebarInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.getNumberWithoutSpacebarByStatus("approved");
        var matchesNumber = cardData.getNumber();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Заполнение поля номера карты c пробелами вначале и в конце")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldSuccessfulWithStartEndSpacebarInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = " " + cardData.getNumber() + " ";
        var matchesNumber = cardData.getNumber();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("11 цифр в поле номера карты")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith11DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWith11Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertNumberFieldIsInvalidValue();
    }

    @Story("12 цифр в поле номера карты")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldUnsuccessfulWith12DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith12Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationWithErrorNotification();
    }

    @Story("13 цифр в поле номера карты")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldUnsuccessfulWith13DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith13Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationWithErrorNotification();
    }

    @Story("18 цифр в поле номера карты")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldUnsuccessfulWith18DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith18Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationWithErrorNotification();
    }

    @Story("19 цифр в поле номера карты")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldUnsuccessfulWith19DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith19Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationWithErrorNotification();
    }

    @Story("20 цифр в поле номера карты")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith20DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWith20Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertNumberFieldIsInvalidValue();
    }

    @Story("Невалидные символы в поле номера карты")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWithRandomSymbols();
        var matchesNumber = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertNumberFieldIsEmptyValue();
    }

    @Story("Пустое поле месяц")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "";
        var matchesMonth = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsEmptyValue();
    }

    @Story("Заполнение поля месяц одной цифрой")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldAddingNullInMonthWith1Digit() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateRandomOneDigit();
        var matchesMonth = "0" + month;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Заполнение поля месяц 3 цифрами")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldDeletingThirdDigitInMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = cardData.getMonth() + DataHelper.generateRandomOneDigit();
        var matchesMonth = cardData.getMonth();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Заполнение поля месяц значением 00")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith00InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "00";
        var matchesMonth = month;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsInvalidValue();
    }

    @Story("Заполнение поля месяц значением 01")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWith01InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "01";
        var matchesMonth = month;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Заполнение поля месяц значением 12")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWith12InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "12";
        var matchesMonth = month;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Заполнение поля месяц значением 13")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith13InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "13";
        var matchesMonth = month;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsInvalidValue();
    }

    @Story("Невалидные символы в поле месяц")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonthWithRandomSymbols();
        var matchesMonth = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsEmptyValue();
    }

    @Story("Пустое поле год")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "";
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsEmptyValue();
    }

    @Story("Заполнение поля год одной цифрой")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithOneDigitInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateRandomOneDigit();
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsInvalidValue();
    }

    @Story("Заполнение поля год 3 цифрами")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldDeletingThirdDigitInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = cardData.getYear() + DataHelper.generateRandomOneDigit();
        var matchesYear = cardData.getYear();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Заполнение поля год 4 цифрами")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldDeletingRedundantDigitsInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "20" + cardData.getYear();
        var matchesYear = "20";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsInvalidValue();
    }

    @Story("Заполнение поля год предыдущим годом")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithPrevYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateYear(-1);
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsInvalidValue();
    }

    @Story("Заполнение поля год текущим годом, поля месяц предыдущим")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithPrevMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonth(-1);
        var matchesMonth = month;
        var year = DataHelper.generateYear(0);
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsInvalidValue();
    }

    @Story("Заполнение поля год текущим годом, поля месяц текущим")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWithCurrentMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonth(0);
        var matchesMonth = month;
        var year = DataHelper.generateYear(0);
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Заполнение поля год на пять лет вперед, поля месяц текущим")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWithDateThroughFiveYears() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonth(0);
        var matchesMonth = month;
        var year = DataHelper.generateYear(5);
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Невалидные символы в поле год")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateMonthWithRandomSymbols();
        var matchesYear = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsEmptyValue();
    }

    @Story("Пустое поле владелец")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "";
        var matchesHolder = holder;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertHolderFieldIsEmptyValue();
    }

    @Story("Дефис в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWithHyphenInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateValidHolderWithDoubleLastName();
        var matchesHolder = holder;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Нижний регистр в поле владелец")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldAutoUpperCaseInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = cardData.getHolder().toLowerCase();
        var matchesHolder = cardData.getHolder();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Пробелы вначале и в конце поля владелец")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldAutoDeletingStartEndHyphenInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = " " + cardData.getHolder() + " ";
        var matchesHolder = cardData.getHolder();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Дефисы вначале и в конце поля владелец")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldAutoDeletingStartEndSpacebarInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "-" + cardData.getHolder() + "-";
        var matchesHolder = cardData.getHolder();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Кириллица в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldVisibleNotificationWithCyrillicInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderWithCyrillicSymbols();
        var matchesHolder = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertHolderFieldIsEmptyValue();
    }

    @Story("Невалидные символы в поле владелец")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateHolderWithInvalidSymbols();
        var matchesHolder = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertHolderFieldIsEmptyValue();
    }

    @Story("Пустое поле CVC/CVV")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "";
        var matchesCvc = cvc;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertCvcFieldIsEmptyValue();
    }

    @Story("2 цифры в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith2DigitsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWith2Digit();
        var matchesCvc = cvc;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertCvcFieldIsInvalidValue();
    }

    @Story("4 цифры в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldSuccessfulWith4DigitsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = cardData.getCvc() + DataHelper.generateRandomOneDigit();
        var matchesCvc = cardData.getCvc();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Невалидные символы в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWithRandomSymbols();
        var matchesCvc = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertCvcFieldIsEmptyValue();
    }
}
