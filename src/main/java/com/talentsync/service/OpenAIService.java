package com.talentsync.service;

import java.util.Map;

public interface OpenAIService {
    
    /**
     * Parses resume text and extracts structured information
     * 
     * @param resumeText The raw text of the resume
     * @return A structured map containing parsed resume information
     */
    Map<String, Object> parseResume(String resumeText);
    
    /**
     * Enriches job data with additional insights and extracted information
     * 
     * @param jobTitle Job title
     * @param company Company name
     * @param jobDescription Job description text
     * @return A structured map containing enriched job data
     */
    Map<String, Object> enrichJobData(String jobTitle, String company, String jobDescription);
    
    /**
     * Calculates the match score between a candidate and job
     * 
     * @param candidateData Structured candidate data including skills and experience
     * @param jobData Structured job data including requirements
     * @return A map containing match score and detailed explanation
     */
    Map<String, Object> calculateMatchDetails(Map<String, Object> candidateData, Map<String, Object> jobData);
}
