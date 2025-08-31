package com.talentsync.service.impl;

import com.talentsync.dto.CandidateDTO;
import com.talentsync.model.Candidate;
import com.talentsync.repository.CandidateRepository;
import com.talentsync.service.CandidateService;
import com.talentsync.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final OpenAIService openAIService;

    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Optional<Candidate> getCandidateByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Candidate createCandidate(CandidateDTO candidateDTO) {
        Candidate candidate = new Candidate();
        BeanUtils.copyProperties(candidateDTO, candidate);
        
        // Parse resume if text is provided
        if (candidateDTO.getResumeText() != null && !candidateDTO.getResumeText().isEmpty()) {
            Map<String, Object> parsedResume = parseResume(candidateDTO.getResumeText());
            candidate.setParsedResume(parsedResume);
        }
        
        return candidateRepository.save(candidate);
    }

    @Override
    @Transactional
    public Candidate updateCandidate(Long id, CandidateDTO candidateDTO) {
        Candidate existingCandidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
        
        BeanUtils.copyProperties(candidateDTO, existingCandidate, "id", "createdAt");
        
        // Parse resume if text is provided and different from existing
        if (candidateDTO.getResumeText() != null && 
            !candidateDTO.getResumeText().equals(existingCandidate.getResumeText())) {
            Map<String, Object> parsedResume = parseResume(candidateDTO.getResumeText());
            existingCandidate.setParsedResume(parsedResume);
        }
        
        return candidateRepository.save(existingCandidate);
    }

    @Override
    @Transactional
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }

    @Override
    public List<Candidate> searchCandidatesByName(String name) {
        return candidateRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Candidate> searchCandidatesByRole(String role) {
        return candidateRepository.findByPreferredRoleOrSkills(role);
    }

    @Override
    public List<Candidate> searchCandidatesByLocation(String location) {
        return candidateRepository.findByPreferredLocation(location);
    }

    @Override
    public Map<String, Object> parseResume(String resumeText) {
        // Use OpenAI to parse resume text
        try {
            return openAIService.parseResume(resumeText);
        } catch (Exception e) {
            // Fallback to basic parsing if AI fails
            Map<String, Object> fallbackParsed = new HashMap<>();
            fallbackParsed.put("raw_text", resumeText);
            fallbackParsed.put("parsing_error", e.getMessage());
            return fallbackParsed;
        }
    }
}
