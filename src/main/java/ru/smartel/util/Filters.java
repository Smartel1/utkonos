package ru.smartel.util;

import ru.smartel.dto.Quotation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * Класс позволяет создавать фильтры
 */
public class Filters {
    public static Predicate<Quotation> weekendFilter() {
        return quotation -> {
            EnumSet<DayOfWeek> weekends = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            return !weekends.contains(quotation.getDate().getDayOfWeek());
        };
    }

    public static Predicate<Quotation> november2014fFilter() {
        return quotation -> {
            LocalDate date = quotation.getDate();
            return date.getMonth().equals(Month.NOVEMBER) && date.getYear() == 2014;
        };
    }

    public static Predicate<Quotation> nameFilter(String name) {
        return quotation -> quotation.getName().equals(name);
    }
}
