package hr.fer.tinfer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Profile user;

    /**
     * URL to the image stored in Supabase Storage.
     * For backwards compatibility, this field can also contain base64 data
     * (detected by checking if it starts with "data:" or "http").
     */
    @NotBlank
    @Column(name = "base64data", nullable = false, columnDefinition = "TEXT")
    private String storageUrl;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    /**
     * Check if this photo uses the new storage URL format or legacy base64.
     */
    public boolean isStorageUrl() {
        return storageUrl != null && storageUrl.startsWith("http");
    }

    /**
     * For backwards compatibility - getter that works with old field name.
     */
    public String getBase64Data() {
        return storageUrl;
    }

    /**
     * For backwards compatibility - setter that works with old field name.
     */
    public void setBase64Data(String data) {
        this.storageUrl = data;
    }
}
