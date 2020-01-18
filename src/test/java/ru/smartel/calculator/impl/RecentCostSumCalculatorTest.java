package ru.smartel.calculator.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.smartel.dto.Quotation;
import ru.smartel.util.Filters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RecentCostSumCalculatorTest {

    @ParameterizedTest
    @MethodSource("source1")
    void whenCalculate_thenReturnCorrectResult(List<String> lines,
                                               Predicate<Quotation> filter,
                                               Double expectedCostSum) {
        RecentCostSumCalculator calculator = new RecentCostSumCalculator(filter);

        RecentCostSumCalculator.RecentQuotationsBatchResult result = calculator.calculate(lines);

        assertThat(result.getCostSum())
                .isEqualTo(expectedCostSum)
                .as("Сумма цен должна совпадать с ожидаемой");
    }

    @ParameterizedTest
    @MethodSource("source2")
    void whenMergeResults_thenReturnCorrectResult(List<String> lines1,
                                                  List<String> lines2,
                                                  Predicate<Quotation> filter,
                                                  Double expectedCostSum) {
        RecentCostSumCalculator calculator = new RecentCostSumCalculator(filter);

        RecentCostSumCalculator.RecentQuotationsBatchResult result1 = calculator.calculate(lines1);
        RecentCostSumCalculator.RecentQuotationsBatchResult result2 = calculator.calculate(lines2);
        RecentCostSumCalculator.RecentQuotationsBatchResult mergedResult = result1.union(result2);

        assertThat(mergedResult.getCostSum())
                .isEqualTo(expectedCostSum)
                .as("Сумма цен должна совпадать с ожидаемой");
    }

    private static Stream<Arguments> source1() {
        return Stream.of(
                Arguments.of(Arrays.asList(
                        "INSTRUMENT1,16-Jun-2005,3",
                        "INSTRUMENT2,16-Jun-2005,30",
                        "INSTRUMENT3,19-Dec-2014,20",
                        "INSTRUMENT3,24-Nov-2000,120",
                        "INSTRUMENT3,04-Dec-2000,100"), Filters.nameFilter("INSTRUMENT3"), 240D),
                Arguments.of(Arrays.asList(
                        "INSTRUMENT1,19-Dec-2014,20",
                        "INSTRUMENT2,24-Nov-2014,120",
                        "INSTRUMENT3,24-Nov-2014,110",
                        "INSTRUMENT4,04-Dec-2000,130"), Filters.november2014fFilter(), 230D),
                Arguments.of(Collections.emptyList(), Filters.weekendFilter(), 0D)
        );
    }

    private static Stream<Arguments> source2() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                "INSTRUMENT1,16-Jun-2005,3",
                                "INSTRUMENT3,16-Jun-2005,30"),
                        Arrays.asList(
                                "INSTRUMENT2,19-Dec-2014,20",
                                "INSTRUMENT3,24-Nov-2000,120",
                                "INSTRUMENT3,04-Dec-2000,130"), Filters.nameFilter("INSTRUMENT3"), 280D),
                Arguments.of(
                        Arrays.asList(
                                "INSTRUMENT3,19-Dec-2014,20",
                                "INSTRUMENT3,19-Nov-2015,20",
                                "INSTRUMENT3,24-Nov-2014,120"),
                        Collections.singletonList("INSTRUMENT3,04-Dec-2000,130"), Filters.november2014fFilter(), 120D),
                Arguments.of(Collections.emptyList(), Collections.emptyList(), Filters.weekendFilter(), 0D)
        );
    }
}
