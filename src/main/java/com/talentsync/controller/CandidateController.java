package com.talentsync.controller;

import com.talentsync.dto.CandidateDTO;
import com.talentsync.model.Candidate;
import com.talentsync.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Long id) {
        return candidateService.getCandidateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Candidate> getCandidateByEmail(@PathVariable String email) {
        return candidateService.getCandidateByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Candidate> createCandidate(@Valid @RequestBody CandidateDTO candidateDTO) {
        Candidate created = candidateService.createCandidate(candidateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(
            @PathVariable Long id,
            @Valid @RequestBody CandidateDTO candidateDTO) {
        try {
            Candidate updated = candidateService.updateCandidate(id, candidateDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<Candidate>> searchCandidatesByName(@PathVariable String name) {
        return ResponseEntity.ok(candidateService.searchCandidatesByName(name));
    }

    @GetMapping("/search/role/{role}")
    public ResponseEntity<List<Candidate>> searchCandidatesByRole(@PathVariable String role) {
        return ResponseEntity.ok(candidateService.searchCandidatesByRole(role));
    }

    @GetMapping("/search/location/{location}")
    public ResponseEntity<List<Candidate>> searchCandidatesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(candidateService.searchCandidatesByLocation(location));
    }

    @PostMapping("/parse-resume")
    public ResponseEntity<Map<String, Object>> parseResume(@RequestBody Map<String, String> request) {
        String resumeText = request.get("resumeText");
        if (resumeText == null || resumeText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(candidateService.parseResume(resumeText));
    }
}
