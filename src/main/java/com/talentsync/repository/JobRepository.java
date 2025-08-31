package com.talentsync.repository;

import com.talentsync.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findByTitleContainingIgnoreCase(String title);
    
    List<Job> findByCompanyContainingIgnoreCase(String company);
    
    List<Job> findByLocationContainingIgnoreCase(String location);
    
    @Query("SELECT j FROM Job j WHERE j.datePosted >= :startDate")
    List<Job> findRecentJobs(LocalDate startDate);
    
    @Query("SELECT j FROM Job j WHERE CAST(j.skills AS text) LIKE %:skill%")
    List<Job> findBySkill(String skill);
    
    @Query("SELECT j FROM Job j WHERE j.description LIKE %:keyword%")
    List<Job> findByKeywordInDescription(String keyword);
}
