package ru.smartel.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Immutable
 */
public final class Quotation {
    // formatter не меняется, поэтому делаем его статической переменной
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US);

    private String name;
    private LocalDate date;
    private Double cost;

    public Quotation(String name, LocalDate date, Double cost) {
        this.name = name;
        this.date = date;
        this.cost = cost;
    }

    /**
     * Десериализовать статистическую запись о цене инструмента
     * @param string CSV формата: "INSTRUMENT1,25-Nov-2014,9.5"
     * @return объект InstrumentCost
     */
    public static Quotation fromCSV(String string) {
        String[] parts = string.split(",");
        return new Quotation(parts[0], LocalDate.parse(parts[1], FORMATTER), Double.valueOf(parts[2]));
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getCost() {
        return cost;
    }
}
