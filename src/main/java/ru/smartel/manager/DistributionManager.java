package ru.smartel.manager;

import ru.smartel.calculator.BatchResult;

/**
 * Позволяет распределять вычисления на несколько исполнителей
 * Сначала построчно добавляем содержимое файла в буфер вызовом addStatisticsLine().
 * При этом периодически запускается обработка данных и очистка буфера.
 * Методом getResult получаем результаты вычислений (блокирующий метод)
 * @param <T>
 */
public interface DistributionManager<T extends BatchResult<T>> {

    /**
     * Добавить в буфер одну строку из файла.
     * Реализация метода должна  при достаточном заполнении буфера.
     * @param line строка с ценой инструмента в формате CSV: "INSTRUMENT2,29-Feb-1996,9.308557969"
     */
    void addStatisticsLine(String line);

    /**
     * Получить результаты расчетов. Если в буфере были элементы, они должны отправиться на обработку
     * Это блокирующий метод.
     * @return результат распределенных вычислений
     */
    T getResult();
}
