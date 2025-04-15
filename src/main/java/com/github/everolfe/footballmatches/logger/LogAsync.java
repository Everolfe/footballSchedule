package com.github.everolfe.footballmatches.logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
            Map<String, String> taskStatus, Map<String, Path> taskFiles,
            final String date) {
        try {
            Thread.sleep(10000); // имитация долгой работы
            Path logFilePath = Paths.get(LOG_FILE_PATH);
            List<String> filteredLines = List.of();

            if (Files.exists(logFilePath)) {
                try (Stream<String> lines = Files.lines(logFilePath)) {
                    filteredLines = lines
                            .filter(line -> line.contains(date))
                            .collect(Collectors.toList());
                }
            }

            if (filteredLines.isEmpty()) {
                taskStatus.put(taskId, "COMPLETED_NO_DATA");
                return;
            }

            Path outputDir = logFilePath.getParent();
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            String fileName = "filtered_logs_" + date + "_" + taskId + ".log";
            Path outputFile = outputDir.resolve(fileName);

            Files.write(
                    outputFile,
                    filteredLines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            // Обновляем статус задачи
            taskFiles.put(taskId, outputFile);
            taskStatus.put(taskId, "COMPLETED");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            taskStatus.put(taskId, "FAILED: " + e.getMessage());
        } catch (Exception e) {
            taskStatus.put(taskId, "FAILED: " + e.getMessage());
        }

    }
}
