package com.talentsync.service.impl;

import com.talentsync.dto.JobDTO;
import com.talentsync.model.Job;
import com.talentsync.repository.JobRepository;
import com.talentsync.service.JobService;
import com.talentsync.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final OpenAIService openAIService;

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    @Override
    @Transactional
    public Job createJob(JobDTO jobDTO) {
        Job job = new Job();
        BeanUtils.copyProperties(jobDTO, job);
        
        // Set the date posted to current date if not provided
        if (job.getDatePosted() == null) {
            job.setDatePosted(LocalDate.now());
        }
        
        // Enrich job data with AI if description is provided
        if (jobDTO.getDescription() != null && !jobDTO.getDescription().isEmpty()) {
            Map<String, Object> enrichedData = enrichJobData(jobDTO);
            job.setEnrichedData(enrichedData);
        }
        
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    public Job updateJob(Long id, JobDTO jobDTO) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
        
        BeanUtils.copyProperties(jobDTO, existingJob, "id", "createdAt");
        
        // Re-enrich job data if description has changed
        if (jobDTO.getDescription() != null && 
            !jobDTO.getDescription().equals(existingJob.getDescription())) {
            Map<String, Object> enrichedData = enrichJobData(jobDTO);
            existingJob.setEnrichedData(enrichedData);
        }
        
        return jobRepository.save(existingJob);
    }

    @Override
    @Transactional
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    @Override
    public List<Job> searchJobsByTitle(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Job> searchJobsByCompany(String company) {
        return jobRepository.findByCompanyContainingIgnoreCase(company);
    }

    @Override
    public List<Job> searchJobsByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    @Override
    public List<Job> getRecentJobs(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return jobRepository.findRecentJobs(startDate);
    }

    @Override
    public List<Job> searchJobsBySkill(String skill) {
        return jobRepository.findBySkill(skill);
    }

    @Override
    public List<Job> searchJobsByKeyword(String keyword) {
        return jobRepository.findByKeywordInDescription(keyword);
    }

    @Override
    public Map<String, Object> enrichJobData(JobDTO jobDTO) {
        // Use OpenAI to extract structured data from job description
        try {
            return openAIService.enrichJobData(
                jobDTO.getTitle(),
                jobDTO.getCompany(),
                jobDTO.getDescription()
            );
        } catch (Exception e) {
            // Return basic info if AI processing fails
            Map<String, Object> basicInfo = Map.of(
                "skills", jobDTO.getSkills() != null ? jobDTO.getSkills() : List.of(),
                "error", "Failed to process with AI: " + e.getMessage()
            );
            return basicInfo;
        }
    }
}
