package com.talentsync.repository;

import com.talentsync.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    
    Optional<Candidate> findByEmail(String email);
    
    List<Candidate> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT c FROM Candidate c WHERE c.preferredRole LIKE %:role% OR CAST(c.parsedResume AS text) LIKE %:role%")
    List<Candidate> findByPreferredRoleOrSkills(String role);
    
    @Query("SELECT c FROM Candidate c WHERE c.preferredLocation LIKE %:location%")
    List<Candidate> findByPreferredLocation(String location);
}
