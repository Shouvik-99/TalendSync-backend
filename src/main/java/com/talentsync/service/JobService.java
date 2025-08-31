package com.talentsync.service;

import com.talentsync.dto.JobDTO;
import com.talentsync.model.Job;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JobService {
    
    List<Job> getAllJobs();
    
    Optional<Job> getJobById(Long id);
    
    Job createJob(JobDTO jobDTO);
    
    Job updateJob(Long id, JobDTO jobDTO);
    
    void deleteJob(Long id);
    
    List<Job> searchJobsByTitle(String title);
    
    List<Job> searchJobsByCompany(String company);
    
    List<Job> searchJobsByLocation(String location);
    
    List<Job> getRecentJobs(int days);
    
    List<Job> searchJobsBySkill(String skill);
    
    List<Job> searchJobsByKeyword(String keyword);
    
    Map<String, Object> enrichJobData(JobDTO jobDTO);
}
