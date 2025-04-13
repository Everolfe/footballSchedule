package com.github.everolfe.footballmatches.logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;
import java.util.Set;

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
            Path tempDir = Path.of(System.getProperty("java.io.tmpdir"));
            Path file = Files.createTempFile(tempDir, "generated_log_" + taskId, ".log");
            Files.setPosixFilePermissions(file,
                    Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
            Files.writeString(file, "Лог-файл сгенерирован. ID: " + taskId,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            taskFiles.put(taskId, file);
            taskStatus.put(taskId, "COMPLETED");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            taskStatus.put(taskId, "FAILED");
        } catch (Exception e) {
            taskStatus.put(taskId, "FAILED");
        }
    }
}
