package com.github.everolfe.footballmatches.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class LogAsync {

    private static final String LOG_FILE_PATH = "./logs/logfile.log";

    @Async
    public void generateLogFileAsync(
            final String taskId,
            Map<String, String> taskStatus,
            Map<String, Path> taskFiles,
            final String date) throws IOException {
        try{
        Thread.sleep(10000);
        Path logFilePath = Paths.get(LOG_FILE_PATH);
        Path datedFilePath = logFilePath.getParent().resolve("logs_" + date + ".log");

        // Если файл для этой даты уже существует - возвращаем его
        if (Files.exists(datedFilePath)) {
            taskFiles.put(taskId, datedFilePath);
            taskStatus.put(taskId, "COMPLETED");
            return;
        }

        // Иначе фильтруем и создаем файл
        List<String> filteredLines = filterLogsByDate(date);

        if (filteredLines.isEmpty()) {
            taskStatus.put(taskId, "COMPLETED_NO_DATA");
            return;
        }

        Files.write(datedFilePath, filteredLines, StandardOpenOption.CREATE_NEW);
        taskFiles.put(taskId, datedFilePath);
        taskStatus.put(taskId, "COMPLETED");
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            taskStatus.put(taskId, "FAILED: " + e.getMessage());
        } catch (Exception e) {
            taskStatus.put(taskId, "FAILED: " + e.getMessage());
        }
    }
    private List<String> filterLogsByDate(String date) throws IOException {
        Path logFilePath = Paths.get(LOG_FILE_PATH);
        if (!Files.exists(logFilePath)) {
            return List.of();
        }

        try (Stream<String> lines = Files.lines(logFilePath)) {
            return lines.filter(line -> line.contains(date))
                    .toList();
        }
    }
}
