package com.talentsync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.TypeDef;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    
    @Column(name = "preferred_role")
    private String preferredRole;
    
    @Column(name = "preferred_location")
    private String preferredLocation;
    
    @Column(name = "resume_text", columnDefinition = "TEXT")
    private String resumeText;
    
    @Type(value = JsonBinaryType.class)
    @Column(name = "parsed_resume", columnDefinition = "jsonb")
    private Map<String, Object> parsedResume;
    
    @Type(value = JsonBinaryType.class)
    @Column(name = "profile_data", columnDefinition = "jsonb")
    private Map<String, Object> profileData;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
