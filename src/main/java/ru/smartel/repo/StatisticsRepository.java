package ru.smartel.repo;

import java.util.stream.Stream;

public interface StatisticsRepository {
    Stream<String> read();
}
