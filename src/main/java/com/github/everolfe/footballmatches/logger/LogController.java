package com.github.everolfe.footballmatches.logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class LogController {

    private final LogService logService;

    @Operation(summary = "Start async task to generate log file")
    @GetMapping("/task/start")
    public ResponseEntity<String> startLogTask() {
        String taskId = logService.startAsyncLogGeneration();
        return ResponseEntity.ok(taskId);
    }

    @Operation(summary = "Get status of async log generation task")
    @GetMapping("/task/status")
    public ResponseEntity<String> getTaskStatus(
            @Parameter(description = "Task ID", required = true)
            @RequestParam String id) {
        String status = logService.getTaskStatus(id);
        return status.equals("NOT_FOUND") ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(status);
    }

    @Operation(summary = "Get generated log file by task ID")
    @GetMapping("/task/file")
    public ResponseEntity<Resource> getTaskFile(
            @Parameter(description = "Task ID", required = true)
            @RequestParam String id) {
        try {
            Resource resource = logService.getTaskFile(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=generated_log_" + id + ".log")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Get log file by date",
            description = "Allow you to get log file by specified date")
    @GetMapping
    public ResponseEntity<Resource> getLogsByDate(
            @Parameter(description = "Log date",
                    example = "2025-02-20",
                    required = true)
            @RequestParam String date) {
        try {
            ByteArrayResource resource = logService.getLogsByDate(date);
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }
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



}
