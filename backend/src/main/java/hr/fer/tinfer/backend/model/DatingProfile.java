package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "dating_profiles", schema = "public")
public class DatingProfile {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Profile profiles;

    @Column(name = "looking_for", length = 50)
    private String lookingFor;

    @Column(name = "gender", length = 50)
    private String gender;

    @ColumnDefault("true")
    @Column(name = "show_gender")
    private Boolean showGender;

    @Column(name = "prompt_1_question", length = 200)
    private String prompt1Question;

    @Column(name = "prompt_1_answer", length = Integer.MAX_VALUE)
    private String prompt1Answer;

    @Column(name = "prompt_2_question", length = 200)
    private String prompt2Question;

    @Column(name = "prompt_2_answer", length = Integer.MAX_VALUE)
    private String prompt2Answer;

    @Column(name = "prompt_3_question", length = 200)
    private String prompt3Question;

    @Column(name = "prompt_3_answer", length = Integer.MAX_VALUE)
    private String prompt3Answer;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

}