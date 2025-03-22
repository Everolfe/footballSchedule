package com.github.everolfe.footballmatches.controllers;

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

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final String LOG_FILE_PATH = "./logs/logfile.log"; // Путь к общему лог-файлу

    @GetMapping
    public ResponseEntity<Resource> getLogsByDate(@RequestParam String date) {
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
