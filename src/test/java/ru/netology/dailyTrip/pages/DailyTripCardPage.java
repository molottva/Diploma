package ru.netology.dailyTrip.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class DailyTripCardPage {
    private static SelenideElement dailyTripHeading = $x("//div[@id='root']/div/h2");
    private static SelenideElement dailyTripCard = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");
    private SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");

    private SelenideElement formHeading = $x("//form//preceding-sibling::h3");
    private SelenideElement form = $x("//form");
    private SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
    private SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");

    public DailyTripCardPage() {
        dailyTripHeading.should(Condition.visible, Condition.text("Путешествие дня"));
        dailyTripCard.should(Condition.visible);

        payButton.should(Condition.visible);
        creditButton.should(Condition.visible);

        formHeading.should(Condition.hidden);
        form.should(Condition.hidden);
        successNotification.should(Condition.hidden);
        errorNotification.should(Condition.hidden);
    }

    public DailyTripFormPage clickPayButton() {
        payButton.click();
        formHeading.should(Condition.visible, Condition.text("Оплата по карте"));
        return new DailyTripFormPage();
    }

    public DailyTripFormPage clickCreditButton() {
        creditButton.click();
        formHeading.should(Condition.visible, Condition.text("Кредит по данным карты"));
        return new DailyTripFormPage();
    }

    public int getAmount() {
        var str = dailyTripCard.$x(".//ul/li[contains(text(), 'руб')]").getText().split(" ");
        return Integer.valueOf(str[1] + str[2]);
    }
}
