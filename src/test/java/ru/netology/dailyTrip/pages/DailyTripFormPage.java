package ru.netology.dailyTrip.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;

public class DailyTripFormPage {
    private static SelenideElement dailyTripCard = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");
    private SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");
    private SelenideElement form = $x("//form");
    private SelenideElement numberLabel = form.$x(".//span[text()='Номер карты']//ancestor::div/span");
    private SelenideElement numberInput = numberLabel.$x(".//ancestor::span//input");
    private SelenideElement monthLabel = form.$x(".//span[text()='Месяц']//ancestor::div/span/span[1]/span");
    private SelenideElement monthInput = monthLabel.$x(".//input");
    private SelenideElement yearLabel = form.$x(".//span[text()='Год']//ancestor::div/span/span[2]/span");
    private SelenideElement yearInput = yearLabel.$x(".//input");
    private SelenideElement holderLabel = form.$x(".//span[text()='Владелец']//ancestor::div/span/span[1]/span");
    private SelenideElement holderInput = holderLabel.$x(".//input");
    private SelenideElement cvcLabel = form.$x(".//span[text()='CVC/CVV']//ancestor::div/span/span[2]/span");
    private SelenideElement cvcInput = cvcLabel.$x(".//input");
    private SelenideElement continuousButton = form.$x(".//span[text()='Продолжить']//ancestor::button");

    private SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
    private SelenideElement successCloseButton = successNotification.$x("./button");
    private SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");
    private SelenideElement errorCloseButton = errorNotification.$x("./button");

    public DailyTripFormPage() {
        dailyTripCard.should(Condition.visible);
        payButton.should(Condition.visible);
        creditButton.should(Condition.visible);

        form.should(Condition.visible);
        successNotification.should(Condition.hidden);
        errorNotification.should(Condition.hidden);
    }

    public void insertingValueInForm(String number, String month, String year, String holder, String cvc) {
        numberLabel.click();
        numberInput.val(number);
        monthLabel.click();
        monthInput.val(month);
        yearLabel.click();
        yearInput.val(year);
        holderLabel.click();
        holderInput.val(holder);
        cvcLabel.click();
        cvcInput.val(cvc);
        continuousButton.click();
    }

    public void matchesByInsertValue(String number, String month, String year, String holder, String cvc) {
        numberInput.should(Condition.value(number));
        monthInput.should(Condition.value(month));
        yearInput.should(Condition.value(year));
        holderInput.should(Condition.value(holder));
        cvcInput.should(Condition.value(cvc));
    }

    public void assertBuyOperationIsSuccessful() {
        successNotification.should(Condition.visible, Duration.ofSeconds(15));
        successNotification.should(Condition.cssClass("notification_visible"));
        successNotification.$x("./div[@class='notification__title']").should(Condition.text("Успешно"));
        successNotification.$x("./div[@class='notification__content']").should(Condition.text("Операция одобрена Банком."));
        successCloseButton.click();
        successNotification.should(Condition.hidden);
    }

    public void assertBuyOperationWithErrorNotification() {
        errorNotification.should(Condition.visible, Duration.ofSeconds(15));
        errorNotification.should(Condition.cssClass("notification_visible"));
        errorNotification.$x("/div[@class='notification__title']").should(Condition.text("Ошибка"));
        errorNotification.$x("/div[@class='notification__content']").should(Condition.text("Ошибка! Банк отказал в проведении операции."));
        errorCloseButton.click();
        errorNotification.should(Condition.hidden);
    }

    public void assertNumberFieldIsEmptyValue() {
        numberLabel.should(Condition.cssClass("input_invalid")).shouldNot(Condition.cssClass("input_has-value"));
        numberLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Поле обязательно для заполнения"));
    }

    public void assertNumberFieldIsInvalidValue() {
        numberLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        numberLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertMonthFieldIsEmptyValue() {
        monthLabel.should(Condition.cssClass("input_invalid")).shouldNot(Condition.cssClass("input_has-value"));
        monthLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Поле обязательно для заполнения"));
    }

    public void assertMonthFieldIsInvalidValue() {
        monthLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        monthLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверно указан срок действия карты"));
    }

    public void assertYearFieldIsEmptyValue() {
        yearLabel.should(Condition.cssClass("input_invalid"));
        yearLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Поле обязательно для заполнения"));
    }

    public void assertYearFieldIsInvalidValue() {
        yearLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        yearLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Истёк срок действия карты"));
    }

    public void assertHolderFieldIsEmptyValue() {
        holderLabel.should(Condition.cssClass("input_invalid")).shouldNot(Condition.cssClass("input_has-value"));
        holderLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Поле обязательно для заполнения"));
    }

    public void assertHolderFieldIsInvalidValue() {
        holderLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        holderLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertCvcFieldIsEmptyValue() {
        cvcLabel.should(Condition.cssClass("input_invalid")).shouldNot(Condition.cssClass("input_has-value"));
        cvcLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Поле обязательно для заполнения"));
    }

    public void assertCvcFieldIsInvalidValue() {
        cvcLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        cvcLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    //todo переделать
    public String getHolder() {
        return holderInput.getValue();
    }
}
