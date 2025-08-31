package com.talentsync.controller;

import com.talentsync.dto.JobDTO;
import com.talentsync.model.Job;
import com.talentsync.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@Valid @RequestBody JobDTO jobDTO) {
        Job created = jobService.createJob(jobDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobDTO jobDTO) {
        try {
            Job updated = jobService.updateJob(id, jobDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<List<Job>> searchJobsByTitle(@PathVariable String title) {
        return ResponseEntity.ok(jobService.searchJobsByTitle(title));
    }

    @GetMapping("/search/company/{company}")
    public ResponseEntity<List<Job>> searchJobsByCompany(@PathVariable String company) {
        return ResponseEntity.ok(jobService.searchJobsByCompany(company));
    }

    @GetMapping("/search/location/{location}")
    public ResponseEntity<List<Job>> searchJobsByLocation(@PathVariable String location) {
        return ResponseEntity.ok(jobService.searchJobsByLocation(location));
    }

    @GetMapping("/search/skill/{skill}")
    public ResponseEntity<List<Job>> searchJobsBySkill(@PathVariable String skill) {
        return ResponseEntity.ok(jobService.searchJobsBySkill(skill));
    }

    @GetMapping("/search/keyword/{keyword}")
    public ResponseEntity<List<Job>> searchJobsByKeyword(@PathVariable String keyword) {
        return ResponseEntity.ok(jobService.searchJobsByKeyword(keyword));
    }

    @GetMapping("/recent/{days}")
    public ResponseEntity<List<Job>> getRecentJobs(@PathVariable int days) {
        return ResponseEntity.ok(jobService.getRecentJobs(days));
    }

    @PostMapping("/enrich")
    public ResponseEntity<Map<String, Object>> enrichJobData(@RequestBody JobDTO jobDTO) {
        return ResponseEntity.ok(jobService.enrichJobData(jobDTO));
    }
}
