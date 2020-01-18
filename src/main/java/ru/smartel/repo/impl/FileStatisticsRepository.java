package ru.smartel.repo.impl;

import ru.smartel.repo.StatisticsRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileStatisticsRepository implements StatisticsRepository {
    private Path path;

    public FileStatisticsRepository(Path path) {
        this.path = path;
    }

    @Override
    public Stream<String> read() {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new IllegalStateException("Произошла ошибка чтения файла");
        }
    }
}
