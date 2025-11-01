package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import hr.fer.tinfer.backend.types.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "reports", indexes = {
        @Index(name = "reports_reporter_id_idx", columnList = "reporter_id"),
        @Index(name = "reports_reported_id_idx", columnList = "reported_id"),
        @Index(name = "idx_reports_status", columnList = "status")
})
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Profile reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id")
    private Profile reported;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;
    @ColumnDefault("now()")
    @Column(name = "reported_at")
    private Instant reportedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;


      @Column(name = "reason", columnDefinition = "report_reason not null")
     private report_reason reason;

      @Column(name = "mode", columnDefinition = "app_mode not null")
     private app_mode mode;

     @Column(name = "status", columnDefinition = "report_status")
     private report_status status;

}