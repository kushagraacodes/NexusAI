package com.javapowered.nexusai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.javapowered.nexusai.model.AIResult;
import com.javapowered.nexusai.model.Message;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Nexus AI Gemini REST integration with multi-turn context, retry handling and model fallback.
 * API key is read only from GEMINI_API_KEY.
 */
public class GeminiService implements AIService {
    private static final String DEFAULT_MODEL = "gemini-3.8-flash";
    private static final List<String> FALLBACK_MODELS = List.of(
            "gemini-3.8-flash", "gemini-3.7-flash", "gemini-3.6-flash");
    private static final int MAX_CONTEXT_MESSAGES = 20;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    @Override
    public AIResult generateResponse(List<Message> conversation, String requestedModel) {
        String apiKey = System.getenv("GEMINI_API_KEY");
        String selected = requestedModel == null || requestedModel.isBlank()
                ? DEFAULT_MODEL : requestedModel;

        String prompt = conversation == null || conversation.isEmpty()
                ? "" : conversation.get(conversation.size() - 1).content();

        if (apiKey == null || apiKey.isBlank()) {
            return AIResult.demo(
                    "[Demo Mode | " + selected + "]\n\n"
                            + "Your prompt was received successfully:\n" + prompt
                            + "\n\nSet GEMINI_API_KEY in your IntelliJ Run Configuration to connect to the real Gemini API.",
                    selected);
        }

        List<String> candidates = orderedModels(selected);
        String lastError = "Unknown Gemini API error.";

        for (int i = 0; i < candidates.size(); i++) {
            String model = candidates.get(i);
            try {
                HttpResult result = requestModel(apiKey, model, conversation);
                if (result.success()) {
                    return new AIResult(result.text(), model, false, i > 0);
                }

                lastError = result.errorMessage();
                if (!isTransient(result.statusCode())) {
                    return new AIResult(formatApiError(result.statusCode(), lastError), model, false, false);
                }

                // A short retry helps with temporary 429/5xx spikes before switching models.
                if (i == 0) sleep(600);
            } catch (Exception e) {
                lastError = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                if (i == candidates.size() - 1) {
                    return new AIResult("Connection error: " + lastError, model, false, i > 0);
                }
            }
        }

        return new AIResult(
                "Nexus AI is temporarily unavailable. Tried: " + String.join(", ", candidates)
                        + "\n\nLast error: " + lastError,
                selected, false, candidates.size() > 1);
    }

    private HttpResult requestModel(String apiKey, String model, List<Message> conversation) throws Exception {
        ObjectNode body = mapper.createObjectNode();
        ArrayNode contents = body.putArray("contents");

        List<Message> source = conversation == null ? List.of() : conversation;
        int start = Math.max(0, source.size() - MAX_CONTEXT_MESSAGES);
        for (int i = start; i < source.size(); i++) {
            Message message = source.get(i);
            ObjectNode content = contents.addObject();
            content.put("role", "assistant".equals(message.role()) ? "model" : "user");
            ArrayNode parts = content.putArray("parts");
            parts.addObject().put("text", message.content());
        }

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(90))
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String message = root.path("error").path("message").asText(response.body());
            return HttpResult.failure(response.statusCode(), message);
        }

        JsonNode parts = root.path("candidates").path(0).path("content").path("parts");
        StringBuilder answer = new StringBuilder();
        if (parts.isArray()) {
            for (JsonNode part : parts) {
                String text = part.path("text").asText("");
                if (!text.isBlank()) answer.append(text);
            }
        }

        if (answer.isEmpty()) return HttpResult.failure(200, "Nexus AI received an empty response from Gemini.");
        return HttpResult.success(answer.toString());
    }

    private List<String> orderedModels(String requested) {
        List<String> models = new ArrayList<>();
        models.add(requested);
        for (String model : FALLBACK_MODELS) {
            if (!models.contains(model)) models.add(model);
        }
        return models;
    }

    private boolean isTransient(int status) {
        return status == 408 || status == 429 || status == 500 || status == 502
                || status == 503 || status == 504;
    }

    private String formatApiError(int status, String message) {
        return "Gemini API error (" + status + "): " + message;
    }

    private void sleep(long millis) {
        try { Thread.sleep(millis); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private record HttpResult(boolean success, int statusCode, String text, String errorMessage) {
        static HttpResult success(String text) { return new HttpResult(true, 200, text, ""); }
        static HttpResult failure(int status, String message) { return new HttpResult(false, status, "", message); }
    }
}
