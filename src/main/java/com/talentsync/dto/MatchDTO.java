package com.talentsync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {

    private Long id;
    
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    private Double matchScore;
    
    private Map<String, Object> matchDetails;
    
    private String candidateFeedback;
    
    private String status;
}
