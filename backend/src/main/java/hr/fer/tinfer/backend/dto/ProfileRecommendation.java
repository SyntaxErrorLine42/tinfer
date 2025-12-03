package hr.fer.tinfer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRecommendation {

    private UUID profileId;
    private String firstName;
    private String lastName;
    private String displayName;
    private String bio;
    private Integer yearOfStudy;
    private Boolean verified;

    private Set<String> sharedInterests;
    private Set<String> candidateInterests;
    private Set<String> departments;

    private String primaryPhotoUrl;
    private List<String> photoGallery;

    private double compatibilityScore;
    private String highlight;
}
