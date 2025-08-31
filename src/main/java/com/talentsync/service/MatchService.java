package com.talentsync.service;

import com.talentsync.dto.MatchDTO;
import com.talentsync.model.Match;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MatchService {
    
    List<Match> getAllMatches();
    
    Optional<Match> getMatchById(Long id);
    
    Match createMatch(MatchDTO matchDTO);
    
    Match updateMatch(Long id, MatchDTO matchDTO);
    
    void deleteMatch(Long id);
    
    List<Match> getMatchesForCandidate(Long candidateId);
    
    List<Match> getMatchesForJob(Long jobId);
    
    Optional<Match> getMatchByCandidateAndJob(Long candidateId, Long jobId);
    
    List<Match> getHighQualityMatches(Double minScore);
    
    List<Match> getTopMatchesForCandidate(Long candidateId, int limit);
    
    List<Match> getTopCandidatesForJob(Long jobId, int limit);
    
    Double calculateMatchScore(Long candidateId, Long jobId);
    
    Map<String, Object> generateMatchDetails(Long candidateId, Long jobId);
}
