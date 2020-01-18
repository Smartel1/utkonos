package ru.smartel.calculator.impl;

import ru.smartel.calculator.BatchResult;
import ru.smartel.calculator.Calculator;
import ru.smartel.dto.Quotation;

import java.util.List;
import java.util.function.Predicate;

public class AverageCostCalculator implements Calculator<AverageCostCalculator.AvgCostBatchResult> {
    private Predicate<Quotation> filter;

    public AverageCostCalculator(Predicate<Quotation> filter) {
        this.filter = filter;
    }

    /**
     * Вычислить среднюю цену для порции данных об инструментах
     * @param lines данные
     * @return среднее за порцию и кол-во учтённых инструментов (не null)
     */
    @Override
    public AvgCostBatchResult calculate(List<String> lines) {
        // Чтобы инкрементировать счётчик в lambda, оборачиваем его в массив
        double[] quotationsCount = new double[]{0};
        Double avgCost = lines.stream()
                .map(Quotation::fromCSV)
                .filter(filter)
                .peek(unused -> quotationsCount[0]++)
                .mapToDouble(Quotation::getCost)
                .average()
                .orElse(0);

        return new AvgCostBatchResult(avgCost, quotationsCount[0]);
    }

    public static class AvgCostBatchResult implements BatchResult<AvgCostBatchResult> {
        private Double avgCost; //средняя цена выборки
        private double weight; // вес выборки (кол-во элементов)

        private AvgCostBatchResult() {
        }

        public AvgCostBatchResult(Double avgCost, double weight) {
            this.avgCost = avgCost;
            this.weight = weight;
        }

        public Double getAvgCost() {
            return avgCost;
        }

        public double getWeight() {
            return weight;
        }

        @Override
        public AvgCostBatchResult union(AvgCostBatchResult another) {
            //Вычисляем среднюю цену, опираясь на вес отдельных результатов
            double resWeight = this.weight + another.weight;
            if (resWeight == 0) {
                return new AvgCostBatchResult(0D, 0);
            }
            Double resAvg = (this.avgCost * this.weight + another.avgCost * another.weight) / resWeight;
            return new AvgCostBatchResult(resAvg, resWeight);
        }
    }
}
