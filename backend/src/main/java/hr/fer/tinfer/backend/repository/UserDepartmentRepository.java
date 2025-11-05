package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.UserDepartment;
import hr.fer.tinfer.backend.model.UserDepartmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDepartmentRepository extends JpaRepository<UserDepartment, UserDepartmentId> {
  }