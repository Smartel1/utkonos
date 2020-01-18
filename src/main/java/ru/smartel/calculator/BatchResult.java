package ru.smartel.calculator;


public interface BatchResult<T extends BatchResult<T>> { //recursive type bound idiom
    /**
     * Объединение с другими результатами вычислений
     * @param another другой объект с результатами
     * @return новый объект, содержащий объединенные данные
     */
    T union(T another);
}
