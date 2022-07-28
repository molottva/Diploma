package ru.netology.dailyTrip.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class TripCardPage {
    private static final SelenideElement dailyTripHeading = $x("//div[@id='root']/div/h2");
    private static final SelenideElement dailyTripCard = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private static final SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");
    private static final SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");

    private static final SelenideElement formHeading = $x("//form//preceding-sibling::h3");
    private static final SelenideElement form = $x("//form");
    private static final SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
    private static final SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");

    public TripCardPage() {
        dailyTripHeading.should(Condition.visible, Condition.text("Путешествие дня"));
        dailyTripCard.should(Condition.visible);

        payButton.should(Condition.visible);
        creditButton.should(Condition.visible);

        formHeading.should(Condition.hidden);
        form.should(Condition.hidden);
        successNotification.should(Condition.hidden);
        errorNotification.should(Condition.hidden);
    }

    public TripFormPage clickPayButton() {
        payButton.click();
        formHeading.should(Condition.visible, Condition.text("Оплата по карте"));
        return new TripFormPage();
    }

    public TripFormPage clickCreditButton() {
        creditButton.click();
        formHeading.should(Condition.visible, Condition.text("Кредит по данным карты"));
        return new TripFormPage();
    }

    public int getAmount() {
        var str = dailyTripCard.$x(".//ul/li[contains(text(), 'руб')]").getText().split(" ");
        return Integer.valueOf(str[1] + str[2]);
    }
}
