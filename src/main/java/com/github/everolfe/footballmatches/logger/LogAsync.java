package com.github.everolfe.footballmatches.logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class LogAsync {

    @Async
    public void generateLogFileAsync(
            String taskId, Map<String, String> taskStatus, Map<String, Path> taskFiles) {
        try {
            Thread.sleep(10000); // имитация долгой работы
            Path file = Files.createTempFile("generated_log_" + taskId, ".log");
            Files.writeString(file, "Лог-файл сгенерирован. ID: " + taskId);
            taskFiles.put(taskId, file);
            taskStatus.put(taskId, "COMPLETED");
        } catch (Exception e) {
            taskStatus.put(taskId, "FAILED");
        }
    }
}
