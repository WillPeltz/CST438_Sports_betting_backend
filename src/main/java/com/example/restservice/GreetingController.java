package com.example.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
public class GreetingController {

	@GetMapping("/seasons")
    public ApiResponse getSeasons() {
        List<String> seasons = List.of("2015", "2016", "2017", "2018", "2019", "2020");
        ApiPayload payload = new ApiPayload(
                200,
                "GET seasons/",
                seasons.size(),
                List.of(),
                seasons
        );
        return new ApiResponse(payload);
    }

    public record ApiResponse(ApiPayload api) {}

    @JsonPropertyOrder({"status", "message", "results", "filters", "seasons"})
    public record ApiPayload(
            int status,
            String message,
            int results,
            List<String> filters,
            List<String> seasons
    ) {}
}
