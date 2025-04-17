package com.github.everolfe.footballmatches.counter;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/stats")
public class CounterController {
    private final RequestCounter requestCounter;

    public CounterController(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @GetMapping
    public Map<String, Integer> getCounts() {
        return requestCounter.getAllCounts();
    }

    @GetMapping("/url")
    public Integer getCountByUrl(@RequestParam("url") String url) {
        return requestCounter.getCount(url);
    }
}
