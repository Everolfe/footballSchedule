package com.github.everolfe.footballmatches.logger;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


@Service
public class LogService {
    private static final String LOG_FILE_PATH = "./logs/logfile.log";

    private final Map<String, String> taskStatus = new ConcurrentHashMap<>();
    private final Map<String, Path> taskFiles = new ConcurrentHashMap<>();
    private final LogAsync logAsync;
    private final AtomicInteger taskCounter = new AtomicInteger(0);

    public LogService(LogAsync logAsyncService) {
        this.logAsync = logAsyncService;
    }

    @AspectAnnotation
    public ByteArrayResource getLogsByDate(String date) throws IOException {
        List<String> filteredLogs = filterLogsByDate(date);
        if (filteredLogs.isEmpty()) {
            return null;
        }
        String logsContent = String.join("\n", filteredLogs);
        return new ByteArrayResource(logsContent.getBytes());
    }

    @AspectAnnotation
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

    @AspectAnnotation
    public String startAsyncLogGeneration(final String date) {
        if (!isValidDate(date)) {
            throw new IllegalArgumentException("Invalid date format");
        }
        String taskId = String.valueOf(taskCounter.incrementAndGet());
        taskStatus.put(taskId, "IN_PROGRESS");
        logAsync.generateLogFileAsync(taskId, taskStatus, taskFiles, date);
        return taskId;
    }

    private boolean isValidDate(String date) {
        return date != null && date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    public String getTaskStatus(String taskId) {
        return taskStatus.getOrDefault(taskId, "NOT_FOUND");
    }

    public Resource getTaskFile(String taskId) throws IOException {
        Path path = taskFiles.get(taskId);
        if (path == null || !Files.exists(path)) {
            throw new NoSuchFileException("Файл не найден для ID: " + taskId);
        }
        return new FileSystemResource(path.toFile());
    }
}
