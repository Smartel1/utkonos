package ru.smartel.calculator;

import java.util.List;

/**
 * Получает список инструментов, возвращает необходимые результаты вычислений
 * @param <T> тип результатов вычислений
 */
public interface Calculator<T extends BatchResult<T>> {
    T calculate(List<String> lines);
}
