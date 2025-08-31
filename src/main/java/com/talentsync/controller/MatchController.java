package com.talentsync.controller;

import com.talentsync.dto.MatchDTO;
import com.talentsync.model.Match;
import com.talentsync.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        return matchService.getMatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Match> createMatch(@Valid @RequestBody MatchDTO matchDTO) {
        try {
            Match created = matchService.createMatch(matchDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(
            @PathVariable Long id,
            @Valid @RequestBody MatchDTO matchDTO) {
        try {
            Match updated = matchService.updateMatch(id, matchDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Match>> getMatchesForCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(matchService.getMatchesForCandidate(candidateId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Match>> getMatchesForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(matchService.getMatchesForJob(jobId));
    }

    @GetMapping("/candidate/{candidateId}/top/{limit}")
    public ResponseEntity<List<Match>> getTopMatchesForCandidate(
            @PathVariable Long candidateId,
            @PathVariable int limit) {
        return ResponseEntity.ok(matchService.getTopMatchesForCandidate(candidateId, limit));
    }

    @GetMapping("/job/{jobId}/top/{limit}")
    public ResponseEntity<List<Match>> getTopCandidatesForJob(
            @PathVariable Long jobId,
            @PathVariable int limit) {
        return ResponseEntity.ok(matchService.getTopCandidatesForJob(jobId, limit));
    }

    @GetMapping("/quality/{minScore}")
    public ResponseEntity<List<Match>> getHighQualityMatches(@PathVariable Double minScore) {
        return ResponseEntity.ok(matchService.getHighQualityMatches(minScore));
    }

    @GetMapping("/check/{candidateId}/{jobId}")
    public ResponseEntity<Match> getMatchByCandidateAndJob(
            @PathVariable Long candidateId,
            @PathVariable Long jobId) {
        return matchService.getMatchByCandidateAndJob(candidateId, jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate-score/{candidateId}/{jobId}")
    public ResponseEntity<Double> calculateMatchScore(
            @PathVariable Long candidateId,
            @PathVariable Long jobId) {
        try {
            Double score = matchService.calculateMatchScore(candidateId, jobId);
            return ResponseEntity.ok(score);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/generate-details/{candidateId}/{jobId}")
    public ResponseEntity<Map<String, Object>> generateMatchDetails(
            @PathVariable Long candidateId,
            @PathVariable Long jobId) {
        try {
            Map<String, Object> details = matchService.generateMatchDetails(candidateId, jobId);
            return ResponseEntity.ok(details);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
