package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
  }