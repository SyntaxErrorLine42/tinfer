package hr.fer.tinfer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dating_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatingProfile {

    @Id
    private UUID userId;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Profile user;

    @Column(name = "looking_for", length = 50)
    private String lookingFor; // relationship, casual, not_sure

    @Column(length = 50)
    private String gender;

    @Column(name = "show_gender")
    private Boolean showGender = true;

    @Column(name = "prompt_1_question", length = 200)
    private String prompt1Question;

    @Column(name = "prompt_1_answer", columnDefinition = "TEXT")
    private String prompt1Answer;

    @Column(name = "prompt_2_question", length = 200)
    private String prompt2Question;

    @Column(name = "prompt_2_answer", columnDefinition = "TEXT")
    private String prompt2Answer;

    @Column(name = "prompt_3_question", length = 200)
    private String prompt3Question;

    @Column(name = "prompt_3_answer", columnDefinition = "TEXT")
    private String prompt3Answer;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
