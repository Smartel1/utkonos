package ru.smartel.calculator.impl;

import ru.smartel.calculator.BatchResult;
import ru.smartel.calculator.Calculator;
import ru.smartel.dto.Quotation;

import java.util.List;
import java.util.function.Predicate;

public class MaxCostCalculator implements Calculator<MaxCostCalculator.MaxCostBatchResult> {
    private Predicate<Quotation> filter;

    public MaxCostCalculator(Predicate<Quotation> filter) {
        this.filter = filter;
    }

    /**
     * Вычислить максимальную цену для порции данных об инструментах
     * @param lines данные
     * @return среднее за порцию и кол-во учтённых инструментов (не null)
     */
    @Override
    public MaxCostBatchResult calculate(List<String> lines) {
        Double maxCost = lines.stream()
                .map(Quotation::fromCSV)
                .filter(filter)
                .mapToDouble(Quotation::getCost)
                .max()
                .orElse(0);

        return new MaxCostBatchResult(maxCost);
    }

    public static class MaxCostBatchResult implements BatchResult<MaxCostBatchResult> {
        private Double maxCost; //максимальная цена выборки

        private MaxCostBatchResult() {
        }

        public MaxCostBatchResult(Double maxCost) {
            this.maxCost = maxCost;
        }

        public Double getMaxCost() {
            return maxCost;
        }

        @Override
        public MaxCostBatchResult union(MaxCostBatchResult another) {
            return new MaxCostBatchResult(Math.max(maxCost, another.maxCost));
        }
    }
}
