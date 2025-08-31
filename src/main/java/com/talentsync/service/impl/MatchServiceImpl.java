package com.talentsync.service.impl;

import com.talentsync.dto.MatchDTO;
import com.talentsync.model.Candidate;
import com.talentsync.model.Job;
import com.talentsync.model.Match;
import com.talentsync.repository.CandidateRepository;
import com.talentsync.repository.JobRepository;
import com.talentsync.repository.MatchRepository;
import com.talentsync.service.MatchService;
import com.talentsync.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final OpenAIService openAIService;

    @Override
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    @Override
    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }

    @Override
    @Transactional
    public Match createMatch(MatchDTO matchDTO) {
        // Validate the candidate and job exist
        Candidate candidate = candidateRepository.findById(matchDTO.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + matchDTO.getCandidateId()));
        
        Job job = jobRepository.findById(matchDTO.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + matchDTO.getJobId()));
        
        // Check if a match already exists
        Optional<Match> existingMatch = matchRepository.findByCandidateAndJob(candidate, job);
        if (existingMatch.isPresent()) {
            throw new RuntimeException("Match already exists between candidate " + 
                    matchDTO.getCandidateId() + " and job " + matchDTO.getJobId());
        }
        
        // Create new match
        Match match = new Match();
        match.setCandidate(candidate);
        match.setJob(job);
        
        // Calculate match score if not provided
        if (matchDTO.getMatchScore() == null) {
            Double score = calculateMatchScore(matchDTO.getCandidateId(), matchDTO.getJobId());
            match.setMatchScore(score);
        } else {
            match.setMatchScore(matchDTO.getMatchScore());
        }
        
        // Generate match details if not provided
        if (matchDTO.getMatchDetails() == null) {
            Map<String, Object> details = generateMatchDetails(matchDTO.getCandidateId(), matchDTO.getJobId());
            match.setMatchDetails(details);
        } else {
            match.setMatchDetails(matchDTO.getMatchDetails());
        }
        
        match.setCandidateFeedback(matchDTO.getCandidateFeedback());
        match.setStatus(matchDTO.getStatus() != null ? matchDTO.getStatus() : "NEW");
        
        return matchRepository.save(match);
    }

    @Override
    @Transactional
    public Match updateMatch(Long id, MatchDTO matchDTO) {
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        
        // Update only fields that can be changed
        if (matchDTO.getMatchScore() != null) {
            existingMatch.setMatchScore(matchDTO.getMatchScore());
        }
        
        if (matchDTO.getMatchDetails() != null) {
            existingMatch.setMatchDetails(matchDTO.getMatchDetails());
        }
        
        if (matchDTO.getCandidateFeedback() != null) {
            existingMatch.setCandidateFeedback(matchDTO.getCandidateFeedback());
        }
        
        if (matchDTO.getStatus() != null) {
            existingMatch.setStatus(matchDTO.getStatus());
        }
        
        return matchRepository.save(existingMatch);
    }

    @Override
    @Transactional
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

    @Override
    public List<Match> getMatchesForCandidate(Long candidateId) {
        return matchRepository.findByCandidateId(candidateId);
    }

    @Override
    public List<Match> getMatchesForJob(Long jobId) {
        return matchRepository.findByJobId(jobId);
    }

    @Override
    public Optional<Match> getMatchByCandidateAndJob(Long candidateId, Long jobId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
        
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));
        
        return matchRepository.findByCandidateAndJob(candidate, job);
    }

    @Override
    public List<Match> getHighQualityMatches(Double minScore) {
        return matchRepository.findHighQualityMatches(minScore);
    }

    @Override
    public List<Match> getTopMatchesForCandidate(Long candidateId, int limit) {
        // This simplistically gets all matches and limits them in memory
        // In a real implementation, you'd want to add a limit clause to the query
        return matchRepository.findTopMatchesForCandidate(candidateId).stream()
                .limit(limit)
                .toList();
    }

    @Override
    public List<Match> getTopCandidatesForJob(Long jobId, int limit) {
        // This simplistically gets all matches and limits them in memory
        // In a real implementation, you'd want to add a limit clause to the query
        return matchRepository.findTopCandidatesForJob(jobId).stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Double calculateMatchScore(Long candidateId, Long jobId) {
        try {
            // Get the candidate and job data
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
            
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));
            
            // Extract structured data
            Map<String, Object> candidateData = candidate.getParsedResume();
            Map<String, Object> jobData = job.getEnrichedData();
            
            // Use AI to calculate match score
            Map<String, Object> matchDetails = openAIService.calculateMatchDetails(candidateData, jobData);
            
            // Extract the score from the response
            if (matchDetails.containsKey("match_score")) {
                return Double.valueOf(matchDetails.get("match_score").toString());
            }
            
            // Fallback to 0.5 if no score is found
            return 0.5;
        } catch (Exception e) {
            // If anything goes wrong, return a neutral score
            return 0.5;
        }
    }

    @Override
    public Map<String, Object> generateMatchDetails(Long candidateId, Long jobId) {
        try {
            // Get the candidate and job data
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
            
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));
            
            // Extract structured data
            Map<String, Object> candidateData = candidate.getParsedResume();
            Map<String, Object> jobData = job.getEnrichedData();
            
            // Use AI to generate match details
            return openAIService.calculateMatchDetails(candidateData, jobData);
        } catch (Exception e) {
            // Return basic info if AI processing fails
            Map<String, Object> basicDetails = new HashMap<>();
            basicDetails.put("match_score", 0.5);
            basicDetails.put("error", "Failed to generate match details: " + e.getMessage());
            return basicDetails;
        }
    }
}
