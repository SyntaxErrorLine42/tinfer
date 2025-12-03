package hr.fer.tinfer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchSummary {

    private Long matchId;
    private UUID partnerId;
    private String partnerFirstName;
    private String partnerLastName;
    private String partnerDisplayName;
    private String partnerBio;
    private Integer partnerYearOfStudy;
    private Boolean partnerVerified;

    private Set<String> sharedInterests;
    private Set<String> partnerDepartments;
    private String primaryPhotoUrl;
    private String highlight;

    private Long conversationId;
    private LocalDateTime conversationLastMessageAt;
    private LocalDateTime matchedAt;
}
