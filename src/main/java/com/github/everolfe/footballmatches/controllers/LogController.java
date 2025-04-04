package com.github.everolfe.footballmatches.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "LogController",
    description = "Allow to get the log file by the specified date")
@RestController
@RequestMapping("/logs")
public class LogController {

    private static final String LOG_FILE_PATH = "./logs/logfile.log"; // Путь к общему лог-файлу

    @Operation(summary = "Get log file by date",
            description = "Allow you to get log file by specified date")
    @GetMapping
    public ResponseEntity<Resource> getLogsByDate(
            @Parameter(description = "Log date",
                    example = "2025-02-20",  // Добавлен пример даты
                    required = true)
            @RequestParam String date) {
        try {
            List<String> filteredLogs = filterLogsByDate(date);
            if (filteredLogs.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String logsContent = String.join("\n", filteredLogs);
            ByteArrayResource resource = new ByteArrayResource(logsContent.getBytes());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=logs_" + date + ".log")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    new ByteArrayResource(("Ошибка чтения логов: " + e.getMessage()).getBytes()));
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
