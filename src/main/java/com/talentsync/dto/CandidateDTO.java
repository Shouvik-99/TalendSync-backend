package com.talentsync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {

    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    
    private String preferredRole;
    
    private String preferredLocation;
    
    private String resumeText;
    
    private Map<String, Object> parsedResume;
    
    private Map<String, Object> profileData;
}
