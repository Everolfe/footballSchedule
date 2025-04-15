package com.github.everolfe.footballmatches.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
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
            Map<String, String> taskStatus,
            Map<String, Path> taskFiles,
            final String date) {
        try {
            simulateLongProcessing(); // имитация долгой работы

            Path logFilePath = getLogFilePath();
            List<String> filteredLines = filterLogsByDate(logFilePath, date);

            if (filteredLines.isEmpty()) {
                updateTaskStatus(taskStatus, taskId, "COMPLETED_NO_DATA");
                return;
            }

            Path outputFile = createFilteredLogFile(logFilePath, date, taskId, filteredLines);

            updateTaskResult(taskStatus, taskFiles, taskId, outputFile, "COMPLETED");

        } catch (Exception e) {
            updateTaskStatus(taskStatus, taskId, "FAILED: " + e.getMessage());
        }
    }

    private void simulateLongProcessing() throws InterruptedException {
        Thread.sleep(10000); // имитация долгой работы
    }

    private Path getLogFilePath() {
        return Paths.get(LOG_FILE_PATH);
    }

    private List<String> filterLogsByDate(Path logFilePath, String date) throws IOException {
        if (!Files.exists(logFilePath)) {
            return List.of();
        }

        try (Stream<String> lines = Files.lines(logFilePath)) {
            return lines.filter(line -> line.contains(date))
                    .collect(Collectors.toList());
        }
    }

    private Path createFilteredLogFile(Path logFilePath, String date, String taskId, List<String> filteredLines)
            throws IOException {
        Path outputDir = ensureOutputDirectoryExists(logFilePath);
        Path outputFile = buildOutputFilePath(outputDir, date, taskId);

        writeFilteredLinesToFile(outputFile, filteredLines);
        return outputFile;
    }

    private Path ensureOutputDirectoryExists(Path logFilePath) throws IOException {
        Path outputDir = logFilePath.getParent();
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        return outputDir;
    }

    private Path buildOutputFilePath(Path outputDir, String date, String taskId) {
        String fileName = "filtered_logs_" + date + "_" + taskId + ".log";
        return outputDir.resolve(fileName);
    }

    private void writeFilteredLinesToFile(Path outputFile, List<String> filteredLines) throws IOException {
        Files.write(
                outputFile,
                filteredLines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private void updateTaskStatus(Map<String, String> taskStatus, String taskId, String status) {
        taskStatus.put(taskId, status);
    }

    private void updateTaskResult(
            Map<String, String> taskStatus,
            Map<String, Path> taskFiles,
            String taskId,
            Path outputFile,
            String status) {
        taskFiles.put(taskId, outputFile);
        taskStatus.put(taskId, status);
    }
}