package ru.netology.dailyTrip.helpers;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static Faker faker = new Faker(Locale.ENGLISH);

    @Value
    public static class CardData {
        private final String number;
        private final String month;
        private final String year;
        private final String holder;
        private final String cvc;
    }

    public static CardData getValidUserWithApprovedCard() {
        return new CardData(getNumberByStatus("approved"), generateMonth(1), generateYear(2),
                generateValidHolder(), generateValidCVC());
    }

    public static CardData getValidUserWithDeclinedCard() {
        return new CardData(getNumberByStatus("declined"), generateMonth(1), generateYear(2),
                generateValidHolder(), generateValidCVC());
    }

    public static String getNumberByStatus(String status) {
        if (status.equalsIgnoreCase("APPROVED")) {
            return "4444 4444 4444 4441";
        } else if (status.equalsIgnoreCase("DECLINED")) {
            return "4444 4444 4444 4442";
        }
        return null;
    }

    public static String generateInvalidCardNumberWith11Digit() {
        return faker.numerify("4444 44## ###");
    }

    public static String generateValidCardNumberWith12Digit() {
        return faker.numerify("4444 44## ####");
    }

    public static String generateValidCardNumberWith13Digit() {
        return faker.numerify("4444 44## #### #");
    }

    public static String generateValidCardNumberWith16Digit() {
        return faker.numerify("4444 44## #### ####");
    }

    public static String generateValidCardNumberWith18Digit() {
        return faker.numerify("4444 44## #### #### ##");
    }

    public static String generateValidCardNumberWith19Digit() {
        return faker.numerify("4444 44## #### #### ###");
    }

    public static String generateInvalidCardNumberWith20Digit() {
        return faker.numerify("4444 44## #### #### ####");
    }

    //todo метод без проблеов

    public static String generateMonth(int shiftMonth) {
        return LocalDate.now().plusMonths(shiftMonth).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateYear(int shiftYear) {
        return LocalDate.now().plusYears(shiftYear).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateValidHolder() {
        return faker.name().fullName().toUpperCase();
    }

    public static String generateInvalidHolderWithCustomLocale(Locale locale) {
        Faker fakerCustomLocale = new Faker(locale);
        return fakerCustomLocale.name().fullName().toUpperCase();
    }

    public static String generateValidCVC() {
        return faker.numerify("###");
    }

    public static String generateInvalidCVCWith2Digit() {
        return faker.numerify("##");
    }

    public static String generateInvalidCVCWith4Digit() {
        return faker.numerify("####");
    }
}
