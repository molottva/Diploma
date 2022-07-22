package ru.netology.dailyTrip.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;

public class DailyTripPage {
    private SelenideElement dailyTripHeading = $x("//div[@id='root']/div/h2");
    private SelenideElement dailyTripCard = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");
    private SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");

    private SelenideElement formHeading = $x("//form//preceding-sibling::h3");
    private SelenideElement form = $x("//form");
    private SelenideElement numberField = form.$x(".//span[text()='Номер карты']//ancestor::div/span");
    private SelenideElement monthField = form.$x(".//span[text()='Месяц']//ancestor::div/span/span[1]/span");
    private SelenideElement yearField = form.$x(".//span[text()='Год']//ancestor::div/span/span[2]/span");
    private SelenideElement holderField = form.$x(".//span[text()='Владелец']//ancestor::div/span/span[1]/span");
    private SelenideElement cvcField = form.$x(".//span[text()='CVC/CVV']//ancestor::div/span/span[2]/span");
    private SelenideElement continuousButton = form.$x(".//button");

    private SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
    private SelenideElement successCloseButton = successNotification.$x("./button");
    private SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");
    private SelenideElement errorCloseButton = errorNotification.$x("./button");

    public DailyTripPage() {
        dailyTripHeading.should(Condition.visible, Condition.text("Путешествие дня"));
        dailyTripCard.should(Condition.visible);

        payButton.should(Condition.visible);
        creditButton.should(Condition.visible);

        form.should(Condition.hidden);
        successNotification.should(Condition.hidden);
        errorNotification.should(Condition.hidden);
    }

    public void clickPayButton() {
        payButton.click();
        formHeading.should(Condition.visible, Condition.text("Оплата по карте"));
        form.should(Condition.visible);
    }

    public void clickPayButtonUseKeyboard() {
        dailyTripCard.pressTab();
        payButton.should(Condition.focused).pressEnter();
        formHeading.should(Condition.visible, Condition.text("Оплата по карте"));
        form.should(Condition.visible);
    }

    public void clickCreditButton() {
        creditButton.click();
        formHeading.should(Condition.visible, Condition.text("Кредит по данным карты"));
        form.should(Condition.visible);
    }

    public void clickCreditButtonUseKeyboard() {
        dailyTripCard.pressTab().pressTab();
        creditButton.should(Condition.focused).pressEnter();
        formHeading.should(Condition.visible, Condition.text("Кредит по данным карты"));
        form.should(Condition.visible);
    }

    public void insert(String number, String month, String year, String holder, String cvc) {
        numberField.val(number);
        monthField.val(month);
        yearField.val(year);
        holderField.val(holder);
        cvcField.val(cvc);
        continuousButton.click();
    }

    public void insertUseKeyboard(String number, String month, String year, String holder, String cvc) {
        creditButton.pressTab();
        numberField.should(Condition.focused).val(number).pressTab();
        monthField.should(Condition.focused).val(month).pressTab();
        yearField.should(Condition.focused).val(year).pressTab();
        holderField.should(Condition.focused).val(holder).pressTab();
        cvcField.should(Condition.focused).val(cvc).pressTab();
        continuousButton.should(Condition.focused).pressEnter();
    }

    public void success() {
        successNotification.should(Condition.visible, Duration.ofSeconds(15));
        successNotification.should(Condition.cssClass("notification_visible"));
        successNotification.$x("/div[@class='notification__title']").should(Condition.text("Успешно"));
        successNotification.$x("/div[@class='notification__content']").should(Condition.text("Операция одобрена Банком."));
        successCloseButton.click();
        successNotification.should(Condition.hidden);
    }

    public void error() {
        errorNotification.should(Condition.visible, Duration.ofSeconds(15));
        errorNotification.should(Condition.cssClass("notification_visible"));
        errorNotification.$x("/div[@class='notification__title']").should(Condition.text("Ошибка"));
        errorNotification.$x("/div[@class='notification__content']").should(Condition.text("Ошибка! Банк отказал в проведении операции."));
        errorCloseButton.click();
        errorNotification.should(Condition.hidden);
    }
}
