package ru.smartel.manager.impl;

import ru.smartel.manager.DistributionManager;
import ru.smartel.calculator.BatchResult;
import ru.smartel.calculator.Calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ExecutorServiceDistributionManager<T extends BatchResult<T>> implements DistributionManager<T> {
    private Calculator<T> calculator;
    private double bufferSize;
    private ExecutorService executorService;

    private List<String> linesBuffer = new ArrayList<>();
    private List<CompletableFuture<T>> calculationFutures = new ArrayList<>();

    public ExecutorServiceDistributionManager(Calculator<T> calculator, double bufferSize, ExecutorService executorService) {
        this.calculator = calculator;
        this.bufferSize = bufferSize;
        this.executorService = executorService;
    }

    @Override
    public void addStatisticsLine(String line) {
        linesBuffer.add(line);
        if (linesBuffer.size() >= bufferSize) {
            flush();
        }
    }

    @Override
    public T getResult() {
        if (!linesBuffer.isEmpty()) {
            flush();
        }
        return calculationFutures.stream()
                .map(CompletableFuture::join)
                .reduce(BatchResult::union)
                .orElseThrow(() -> new IllegalStateException("Результат вычислений не должен быть null"));
    }

    /**
     * Отправить данные из буфера на вычисления
     */
    private void flush() {
        List<String> linesBufferCopy = new ArrayList<>(linesBuffer);
        CompletableFuture<T> calculationFuture = CompletableFuture.supplyAsync(
                () -> calculator.calculate(linesBufferCopy), executorService);
        calculationFutures.add(calculationFuture);
        linesBuffer.clear();
    }
}
