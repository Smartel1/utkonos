package ru.smartel.calculator.impl;

import ru.smartel.calculator.BatchResult;
import ru.smartel.calculator.Calculator;
import ru.smartel.dto.Quotation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecentCostSumCalculator implements Calculator<RecentCostSumCalculator.RecentQuotationsBatchResult> {
    private Predicate<Quotation> filter;

    public RecentCostSumCalculator(Predicate<Quotation> filter) {
        this.filter = filter;
    }

    /**
     * Найти 10 самых новых котировок для порции данных об инструментах
     * (Вполне может быть, что в выборке меньше данных, чем за 10 дней)
     * @param lines данные
     * @return среднее за порцию и кол-во учтённых инструментов (не null)
     */
    @Override
    public RecentQuotationsBatchResult calculate(List<String> lines) {
        List<Quotation> recentQuotations = lines.stream()
                .map(Quotation::fromCSV)
                .filter(filter)
                .sorted(Comparator.comparing(Quotation::getDate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return new RecentQuotationsBatchResult(recentQuotations);
    }

    public static class RecentQuotationsBatchResult implements BatchResult<RecentQuotationsBatchResult> {
        private List<Quotation> topQuotations = new ArrayList<>(10); //список самых новых котировок

        private RecentQuotationsBatchResult() {
        }

        public RecentQuotationsBatchResult(List<Quotation> topQuotations) {
            this.topQuotations = topQuotations;
        }

        public Double getCostSum() {
            return topQuotations.stream().map(Quotation::getCost).reduce(0D, Double::sum);
        }

        @Override
        public RecentQuotationsBatchResult union(RecentQuotationsBatchResult another) {
            // объединяем две коллекции, сортируем по дате и выбираем 10 самых новых
            List<Quotation> merged = Stream.concat(topQuotations.stream(), another.topQuotations.stream())
                    .sorted(Comparator.comparing(Quotation::getDate).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
            return new RecentQuotationsBatchResult(merged);
        }
    }
}
