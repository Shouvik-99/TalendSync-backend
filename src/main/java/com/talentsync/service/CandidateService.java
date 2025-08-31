package com.talentsync.service;

import com.talentsync.dto.CandidateDTO;
import com.talentsync.model.Candidate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CandidateService {
    
    List<Candidate> getAllCandidates();
    
    Optional<Candidate> getCandidateById(Long id);
    
    Optional<Candidate> getCandidateByEmail(String email);
    
    Candidate createCandidate(CandidateDTO candidateDTO);
    
    Candidate updateCandidate(Long id, CandidateDTO candidateDTO);
    
    void deleteCandidate(Long id);
    
    List<Candidate> searchCandidatesByName(String name);
    
    List<Candidate> searchCandidatesByRole(String role);
    
    List<Candidate> searchCandidatesByLocation(String location);
    
    Map<String, Object> parseResume(String resumeText);
}
