package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Report;
import hr.fer.tinfer.backend.types.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReportedId(UUID reportedId);

    List<Report> findByStatus(ReportStatus status);
}
