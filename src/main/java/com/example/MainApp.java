package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value; // Import for @Value
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Main Spring Boot application class.
 * This file combines both the entry point (@SpringBootApplication)
 * and the REST controller (@RestController).
 */
@SpringBootApplication
@RestController
public class MainApp {

    // 1. FIX: Use @Value to inject the environment variable into a class field.
    // Spring manages the initialization, making the key accessible in all controller methods.
    @Value("${GEMINI_PERSONAL_API_KEY:}")
    private String apiKey;

    // Main method to start the Spring Boot application
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
        // The API key is now loaded by Spring, so no need to load it here in main.
    }

    /**
     * Defines a GET endpoint at the root path ("/").
     * @return A simple introduction message.
     */
    @GetMapping("/")
    public String introMsg() {
        return "This is the Job Search Buddy! I'm here to help you with targeting jobs and industries for your job search!";
    }

    /**
     * Defines a GET endpoint to demonstrate an API call to Gemini.
     * @return The AI's response or an error message.
     */
    @GetMapping("/targetindustries")
    public String targetIndustries() {
        // 2. FIX: apiKey is now a class member and available here.

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("ERROR: The GEMINI_PERSONAL_API_KEY environment variable was not found or is empty.");
            return "ERROR: Gemini API Key not configured. Please set the GEMINI_PERSONAL_API_KEY environment variable.";
        }

        String prompt = "Create a comprehensive table that maps the following 12 industries and their related \"Tech\" sub-industries to the US states where they are most heavily concentrated: Construction, Healthcare, Operations, Transportation, Travel, Education, and their corresponding \"Tech\" versions (e.g., ConstructionTech). For each state listed, assume the concentration is high enough to support at least 10 major companies in that sector. Use a table format with US States in the first column and the corresponding concentrated industries in the second.";
        String jsonPayload = String.format("""
            {
                "contents": [
                    {
                        "parts": [{"text": "%s"}]
                    }
                ]
            }
            """, prompt.replace("\"", "\\\"")); // Escape quotes in the prompt

        HttpClient client = HttpClient.newHttpClient();
        String myURL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myURL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                System.out.println("API Call successful.");
                // NOTE: In a real app, you would parse the JSON response to extract the text.
                return "Gemini API Response (Raw JSON): " + response.body();
            } else {
                System.err.println("API Call failed with status: " + response.statusCode());
                return "API Call failed. Status Code: " + response.statusCode() + ". Body: " + response.body();
            }

        } catch (Exception e) {
            System.err.println("Exception during API call: " + e.getMessage());
            return "An exception occurred while contacting the API: " + e.getMessage();
        }
    }

    /**
     * Defines a GET endpoint at "/similarjobtitles".
     * @return A placeholder message.
     */
    @GetMapping("/similarjobtitles")
    public String similarJobTitles() {
        String prompt = """Based on the submitted job title, gives several related job titles and lists the common skills among all of them and the particular skills required for each one. 
            job title: \"software engineer\"
            job level: \"entry level\"""";
                
        String jsonPayload = String.format("""
            {
                "contents": [
                    {
                        "parts": [{"text": "%s"}]
                    }
                ]
            }
            """, prompt.replace("\"", "\\\"")); // Escape quotes in the prompt

        HttpClient client = HttpClient.newHttpClient();
        String myURL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myURL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                System.out.println("API Call successful.");
                // NOTE: In a real app, you would parse the JSON response to extract the text.
                return "Gemini API Response (Raw JSON): " + response.body();
            } else {
                System.err.println("API Call failed with status: " + response.statusCode());
                return "API Call failed. Status Code: " + response.statusCode() + ". Body: " + response.body();
            }

        } catch (Exception e) {
            System.err.println("Exception during API call: " + e.getMessage());
            return "An exception occurred while contacting the API: " + e.getMessage();
        }

        // return "This feature will provide similar job titles based on your current role!";
    }
}