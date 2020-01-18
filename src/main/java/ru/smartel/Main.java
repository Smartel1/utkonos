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

        // Читаем файл всякий раз, когда хотим что-нибудь вычислить, так как мы не храним данные в памяти.
        // Если бы было необходимо за одно прочтение файла вычислять разные данные, то можно бы было сделать реализацию
        // DistributionManager так, чтобы он отправлял данные сразу в несколько калькуляторов
        printAverageInstrument1Cost(repository);
        printAverageNovInstrument2Cost(repository);
        printMaxInstrument3Cost(repository);
        printRecentInstrument3CostSum(repository);
    }

    /**
     * Вычислить и вывести среднюю стоимость инструмента 1 за всё время
     */
    private static void printAverageInstrument1Cost(StatisticsRepository repository) {
        // Используем try-with-resources для корректного завершения работы с файлом
        try (Stream<String> lines = repository.read()) {
            DistributionManager<AverageCostCalculator.AvgCostBatchResult> manager =
                    new ExecutorServiceDistributionManager<>(
                            new AverageCostCalculator(
                                    Filters.weekendFilter()
                                            .and(Filters.nameFilter("INSTRUMENT1"))),
                            100,
                            Executors.newFixedThreadPool(THREADS_COUNT));

            lines.forEach(manager::addStatisticsLine);
            AverageCostCalculator.AvgCostBatchResult result = manager.getResult();
            System.out.println("Средняя цена INSTRUMENT1: " + result.getAvgCost());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Вычислить и вывести среднюю стоимость инструмента 2 за ноябрь 2014
     */
    private static void printAverageNovInstrument2Cost(StatisticsRepository repository) {
        // Используем try-with-resources для корректного завершения работы с файлом
        try (Stream<String> lines = repository.read()) {
            DistributionManager<AverageCostCalculator.AvgCostBatchResult> manager =
                    new ExecutorServiceDistributionManager<>(
                            new AverageCostCalculator(
                                    Filters.weekendFilter()
                                            .and(Filters.nameFilter("INSTRUMENT2"))
                                            .and(Filters.november2014fFilter())),
                            100,
                            Executors.newFixedThreadPool(THREADS_COUNT));

            lines.forEach(manager::addStatisticsLine);
            AverageCostCalculator.AvgCostBatchResult result = manager.getResult();
            System.out.println("Средняя цена INSTRUMENT2 за ноябрь 2014: " + result.getAvgCost());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Вычислить и вывести максимальную стоимость инструмента 3 за всё время
     */
    private static void printMaxInstrument3Cost(StatisticsRepository repository) {
        // Используем try-with-resources для корректного завершения работы с файлом
        try (Stream<String> lines = repository.read()) {
            DistributionManager<MaxCostCalculator.MaxCostBatchResult> manager =
                    new ExecutorServiceDistributionManager<>(
                            new MaxCostCalculator(
                                    Filters.weekendFilter()
                                            .and(Filters.nameFilter("INSTRUMENT3"))),
                            100,
                            Executors.newFixedThreadPool(THREADS_COUNT));

            lines.forEach(manager::addStatisticsLine);
            MaxCostCalculator.MaxCostBatchResult result = manager.getResult();
            System.out.println("Максимальная цена INSTRUMENT3: " + result.getMaxCost());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Вычислить и вывести сумму стоимостей инструмента 3 за последние 10 дней
     */
    private static void printRecentInstrument3CostSum(StatisticsRepository repository) {
        // Используем try-with-resources для корректного завершения работы с файлом
        try (Stream<String> lines = repository.read()) {
            DistributionManager<RecentCostSumCalculator.RecentQuotationsBatchResult> manager =
                    new ExecutorServiceDistributionManager<>(
                            new RecentCostSumCalculator(
                                    Filters.weekendFilter()
                                            .and(Filters.nameFilter("INSTRUMENT3"))),
                            100,
                            Executors.newFixedThreadPool(THREADS_COUNT));

            lines.forEach(manager::addStatisticsLine);
            RecentCostSumCalculator.RecentQuotationsBatchResult result = manager.getResult();
            System.out.println("Сумма цен за последние 10 дней INSTRUMENT3: " + result.getCostSum());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
