package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Integer> {
}