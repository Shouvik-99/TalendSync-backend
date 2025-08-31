package com.talentsync.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentsync.service.OpenAIService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Override
    public Map<String, Object> parseResume(String resumeText) {
        String prompt = buildResumeParsingPrompt(resumeText);
        String response = getOpenAiResponse(prompt);
        return parseJsonResponse(response);
    }

    @Override
    public Map<String, Object> enrichJobData(String jobTitle, String company, String jobDescription) {
        String prompt = buildJobEnrichmentPrompt(jobTitle, company, jobDescription);
        String response = getOpenAiResponse(prompt);
        return parseJsonResponse(response);
    }

    @Override
    public Map<String, Object> calculateMatchDetails(Map<String, Object> candidateData, Map<String, Object> jobData) {
        try {
            String candidateJson = objectMapper.writeValueAsString(candidateData);
            String jobJson = objectMapper.writeValueAsString(jobData);
            String prompt = buildMatchCalculationPrompt(candidateJson, jobJson);
            String response = getOpenAiResponse(prompt);
            return parseJsonResponse(response);
        } catch (JsonProcessingException e) {
            log.error("Error converting data to JSON", e);
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("match_score", 0.0);
            fallback.put("error", "Failed to process candidate or job data");
            return fallback;
        }
    }

    private String buildResumeParsingPrompt(String resumeText) {
        return "Extract structured information from the following resume. " +
                "Format the output as JSON with the following fields: " +
                "name, email, phone, skills (as array), education (as array of objects with degree, institution, years), " +
                "experience (as array of objects with title, company, years, responsibilities). " +
                "Do not include any other text or explanations.\n\nResume:\n" + resumeText;
    }

    private String buildJobEnrichmentPrompt(String jobTitle, String company, String jobDescription) {
        return "Analyze the following job listing and extract structured information. " +
                "Format the output as JSON with the following fields: " +
                "keywords (as array), required_skills (as array), job_category, seniority_level, " +
                "estimated_years_experience, job_type (full-time, part-time, etc), " +
                "remote_options (yes, no, hybrid). Do not include any other text or explanations.\n\n" +
                "Job Title: " + jobTitle + "\n" +
                "Company: " + company + "\n" +
                "Job Description: " + jobDescription;
    }

    private String buildMatchCalculationPrompt(String candidateJson, String jobJson) {
        return "Calculate the match score between the candidate and job. " +
                "Format the output as JSON with fields: match_score (0.0 to 1.0), " +
                "matching_skills (array), missing_skills (array), " +
                "experience_match (text explanation), education_match (text explanation), " +
                "overall_assessment (text). Do not include any other text or explanations.\n\n" +
                "Candidate: " + candidateJson + "\n\n" +
                "Job: " + jobJson;
    }

    private String getOpenAiResponse(String prompt) {
        try {
            // Create OpenAI service
            OpenAiService service = new OpenAiService(apiKey);
            
            // For version 0.16.1 we're using completion request
            // with specific prompting to get JSON responses
            CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Return only valid JSON. " + prompt)
                .model(model)
                .temperature(0.3)
                .maxTokens(1000)
                .topP(1.0)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build();
            
            CompletionResult result = service.createCompletion(completionRequest);
            return result.getChoices().get(0).getText().trim();
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            return "{ \"error\": \"Failed to process with AI\" }";
        }
    }

    private Map<String, Object> parseJsonResponse(String jsonResponse) {
        try {
            // Strip any non-JSON text that might be in the response
            String cleanJson = extractJsonFromText(jsonResponse);
            return objectMapper.readValue(cleanJson, Map.class);
        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", jsonResponse, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to parse AI response");
            error.put("raw_response", jsonResponse);
            return error;
        }
    }
    
    private String extractJsonFromText(String text) {
        // Simple method to extract JSON from potential text
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        
        if (start >= 0 && end >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        
        return "{}";
    }
}
