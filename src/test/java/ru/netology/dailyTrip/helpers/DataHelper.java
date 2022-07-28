package ru.netology.dailyTrip.helpers;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static final Faker faker = new Faker(Locale.ENGLISH);
    private static final Faker fakerWithCyrillicLocale = new Faker(new Locale("ru", "RU"));

    @Value
    public static class CardData {
        private final String number;
        private final String month;
        private final String year;
        private final String holder;
        private final String cvc;
    }

    public static CardData getValidApprovedCard() {
        return new CardData(getNumberByStatus("approved"), generateMonth(1), generateYear(2),
                generateValidHolder(), generateValidCVC());
    }

    public static CardData getValidDeclinedCard() {
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

    public static String getNumberWithoutSpacebarByStatus(String status) {
        if (status.equalsIgnoreCase("APPROVED")) {
            return "4444444444444441";
        } else if (status.equalsIgnoreCase("DECLINED")) {
            return "4444444444444442";
        }
        return null;
    }

    public static String generateInvalidCardNumberWith11Digits() {
        return faker.numerify("4444 44## ###");
    }

    public static String generateValidCardNumberWith12Digits() {
        return faker.numerify("4444 44## ####");
    }

    public static String generateValidCardNumberWith13Digits() {
        return faker.numerify("4444 44## #### #");
    }

    public static String generateValidCardNumberWith18Digits() {
        return faker.numerify("4444 44## #### #### ##");
    }

    public static String generateValidCardNumberWith19Digits() {
        return faker.numerify("4444 44## #### #### ###");
    }

    public static String generateInvalidCardNumberWith20Digits() {
        return faker.numerify("4444 44## #### #### ####");
    }

    public static String generateInvalidCardNumberWithRandomSymbols() {
        return faker.letterify("???? ???? ???? ????");
    }

    public static String generateMonth(int shiftMonth) {
        return LocalDate.now().plusMonths(shiftMonth).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateMonthWithRandomSymbols() {
        return faker.letterify("??");
    }

    public static String generateYear(int shiftYear) {
        return LocalDate.now().plusYears(shiftYear).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateValidHolder() {
        return faker.name().fullName().toUpperCase();
    }

    public static String generateValidHolderWithDoubleLastName() {
        return faker.name().lastName().toUpperCase() + "-" + faker.name().lastName().toUpperCase() + " "
                + faker.name().firstName().toUpperCase();
    }

    public static String generateInvalidHolderWithCyrillicSymbols() {
        return fakerWithCyrillicLocale.name().firstName().toUpperCase() + " "
                + fakerWithCyrillicLocale.name().lastName().toUpperCase();
    }

    public static String generateHolderWithInvalidSymbols() {
        return faker.numerify("#### #### #### ####");
    }

    public static String generateValidCVC() {
        return faker.numerify("###");
    }

    public static String generateInvalidCVCWith2Digit() {
        return faker.numerify("##");
    }

    public static String generateInvalidCVCWithRandomSymbols() {
        return faker.letterify("???");
    }

    public static String generateRandomOneDigit() {
        return faker.numerify("#");
    }
}
