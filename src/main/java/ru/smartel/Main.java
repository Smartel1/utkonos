package ru.smartel;

import ru.smartel.manager.DistributionManager;
import ru.smartel.manager.impl.ExecutorServiceDistributionManager;
import ru.smartel.calculator.impl.AverageCostCalculator;
import ru.smartel.calculator.impl.MaxCostCalculator;
import ru.smartel.calculator.impl.RecentCostSumCalculator;
import ru.smartel.repo.StatisticsRepository;
import ru.smartel.repo.impl.FileStatisticsRepository;
import ru.smartel.util.Filters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Main {
    private static final int THREADS_COUNT = 2;  //число потоков для выполнения расчетов

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Не указан путь до файла!");
            return;
        }
        Path path = Paths.get(args[0]);
        if (!path.toFile().exists() || !path.toFile().isFile()) {
            System.out.println("Указан некорректный файл!");
            return;
        }
        StatisticsRepository repository = new FileStatisticsRepository(path);

        // Используем try-with-resources для корректного завершения работы с файлом
        // Читаем файл всякий раз, когда хотим что-нибудь вычислить, так как мы не храним данные в памяти
        // Если бы было необходимо за одно прочтение файла вычислять разные данные, то можно бы было сделать реализацию
        // DistributionManager так, чтобы он отправлял данные сразу в несколько калькуляторов
        try (Stream<String> lines = repository.read()) {
            printAverageInstrument1Cost(lines);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try (Stream<String> lines = repository.read()) {
            printAverageNovInstrument2Cost(lines);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try (Stream<String> lines = repository.read()) {
            printMaxInstrument3Cost(lines);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try (Stream<String> lines = repository.read()) {
            printRecentInstrument3CostSum(lines);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Вычислить и вывести среднюю стоимость инструмента 1 за всё время
     */
    private static void printAverageInstrument1Cost(Stream<String> lines) {
        DistributionManager<AverageCostCalculator.AvgCostBatchResult> calculator =
                new ExecutorServiceDistributionManager<>(
                        new AverageCostCalculator(
                                Filters.weekendFilter()
                                        .and(Filters.nameFilter("INSTRUMENT1"))),
                        100,
                        Executors.newFixedThreadPool(THREADS_COUNT));

        lines.forEach(calculator::addStatisticsLine);

        System.out.println("Средняя цена INSTRUMENT1: " + calculator.getResult().getAvgCost());
    }

    /**
     * Вычислить и вывести среднюю стоимость инструмента 2 за ноябрь 2014
     */
    private static void printAverageNovInstrument2Cost(Stream<String> lines) {
        DistributionManager<AverageCostCalculator.AvgCostBatchResult> calculator =
                new ExecutorServiceDistributionManager<>(
                        new AverageCostCalculator(
                                Filters.weekendFilter()
                                        .and(Filters.nameFilter("INSTRUMENT2"))
                                        .and(Filters.november2014fFilter())),
                        100,
                        Executors.newFixedThreadPool(THREADS_COUNT));

        lines.forEach(calculator::addStatisticsLine);

        System.out.println("Средняя цена INSTRUMENT2 за ноябрь 2014: " + calculator.getResult().getAvgCost());
    }

    /**
     * Вычислить и вывести максимальную стоимость инструмента 3 за всё время
     */
    private static void printMaxInstrument3Cost(Stream<String> lines) {
        DistributionManager<MaxCostCalculator.MaxCostBatchResult> calculator =
                new ExecutorServiceDistributionManager<>(
                        new MaxCostCalculator(
                                Filters.weekendFilter()
                                        .and(Filters.nameFilter("INSTRUMENT3"))),
                        100,
                        Executors.newFixedThreadPool(THREADS_COUNT));

        lines.forEach(calculator::addStatisticsLine);

        System.out.println("Максимальная цена INSTRUMENT3: " + calculator.getResult().getMaxCost());
    }

    /**
     * Вычислить и вывести сумму стоимостей инструмента 3 за последние 10 дней
     */
    private static void printRecentInstrument3CostSum(Stream<String> lines) {
        DistributionManager<RecentCostSumCalculator.RecentQuotationsBatchResult> calculator =
                new ExecutorServiceDistributionManager<>(
                        new RecentCostSumCalculator(
                                Filters.weekendFilter()
                                        .and(Filters.nameFilter("INSTRUMENT3"))),
                        100,
                        Executors.newFixedThreadPool(THREADS_COUNT));

        lines.forEach(calculator::addStatisticsLine);

        System.out.println("Сумма цен за последние 10 дней INSTRUMENT3: " + calculator.getResult().getCostSum());
    }
}
