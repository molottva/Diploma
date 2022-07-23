package ru.netology.dailyTrip.helpers;

import com.github.javafaker.Faker;
import lombok.Value;
import ru.netology.dailyTrip.page.DailyTripPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static Faker faker = new Faker(Locale.ENGLISH);

    @Value
    public static class UserData {
        private final String number;
        private final String month;
        private final String year;
        private final String holder;
        private final String cvc;
    }

    public static UserData getValidUserWithApprovedCard() {
        return new UserData(getNumberByStatus("approved"), generateMonth(1), generateYear(2),
                generateHolder(), generateCVC(3));
    }

    public static UserData getValidUserWithDeclinedCard() {
        return new UserData(getNumberByStatus("declined"), generateMonth(1), generateYear(2),
                generateHolder(), generateCVC(3));
    }

    public static String getNumberByStatus(String status) {
        status = status.toUpperCase();
        if (status.equals("APPROVED")) {
            return "4444 4444 4444 4441";
        } else if (status.equals("DECLINED")) {
            return "4444 4444 4444 4442";
        }
        return null;
    }

    public static String generateCardNumber(int quantity) {
        return generateDigit(quantity);
    }

    public static String generateMonth(int shiftMonth) {
        return LocalDate.now().plusMonths(shiftMonth).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateYear(int shiftYear) {
        return LocalDate.now().plusYears(shiftYear).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateHolder() {
        return faker.name().fullName();
    }

    public static String generateHolder(Locale locale) {
        Faker fakerCustomLocale = new Faker(locale);
        return fakerCustomLocale.name().fullName();
    }

    public static String generateCVC(int quantity) {
        return generateDigit(quantity);
    }

    public static String generateDigit(int quantity) {
        String tmp = "";
        for (int i = 0; i < quantity; i++) {
            tmp = tmp + String.valueOf(faker.number().randomDigit());
        }
        return tmp;
    }
}
