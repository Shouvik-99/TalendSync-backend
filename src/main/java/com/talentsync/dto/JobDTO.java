package com.talentsync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

    private Long id;
    
    @NotBlank(message = "Job title is required")
    private String title;
    
    @NotBlank(message = "Company name is required")
    private String company;
    
    private String location;
    
    private String description;
    
    private String source;
    
    private String url;
    
    private LocalDate datePosted;
    
    private List<String> skills;
    
    private String salaryRange;
    
    private Map<String, Object> enrichedData;
}
