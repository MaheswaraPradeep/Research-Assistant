package com.example.researchassistant.service;

import com.example.researchassistant.ResearchRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    private final WebClient webClient;

    public ResearchService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    public String processContent(ResearchRequest researchRequest) {
        //building prompt
        String prompt=buildPrompt(researchRequest);
        //query the api
        Map<String,Object> requestBody=Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt),
                        })
                }
        );
        String response=webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        //parse the response
        //return  the response
        return extractText(response);
    }

    private String extractText(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                return "No candidates found in response";
            }

            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (parts.isEmpty()) {
                return "No parts found in candidate response";
            }

            return parts.get(0).path("text").asText();
        } catch (Exception e) {
            return "Error parsing: " + e.getMessage();
        }
    }


    private String buildPrompt(ResearchRequest researchRequest) {
        StringBuilder prompt = new StringBuilder();
        switch(researchRequest.getOperation()){
            case "summarize":
                prompt.append("provide a clear and concise summary of the following content in few sentences:\n\n");
                break;
            case "suggest":
                prompt.append("Based on the following content, provide relevant suggestions or recommendations for improvement:\n\n\"");
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + researchRequest.getOperation());
        }
        prompt.append(researchRequest.getContent());
        return prompt.toString();
    }
}
