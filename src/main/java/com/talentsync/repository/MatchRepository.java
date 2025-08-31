package com.talentsync.repository;

import com.talentsync.model.Candidate;
import com.talentsync.model.Job;
import com.talentsync.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findByCandidate(Candidate candidate);
    
    List<Match> findByJob(Job job);
    
    Optional<Match> findByCandidateAndJob(Candidate candidate, Job job);
    
    @Query("SELECT m FROM Match m WHERE m.candidate.id = :candidateId")
    List<Match> findByCandidateId(Long candidateId);
    
    @Query("SELECT m FROM Match m WHERE m.job.id = :jobId")
    List<Match> findByJobId(Long jobId);
    
    @Query("SELECT m FROM Match m WHERE m.matchScore >= :minScore ORDER BY m.matchScore DESC")
    List<Match> findHighQualityMatches(Double minScore);
    
    @Query("SELECT m FROM Match m WHERE m.candidate.id = :candidateId ORDER BY m.matchScore DESC")
    List<Match> findTopMatchesForCandidate(Long candidateId);
    
    @Query("SELECT m FROM Match m WHERE m.job.id = :jobId ORDER BY m.matchScore DESC")
    List<Match> findTopCandidatesForJob(Long jobId);
}
