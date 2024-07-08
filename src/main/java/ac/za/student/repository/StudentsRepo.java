package ac.za.student.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.za.student.entity.Students;



public interface StudentsRepo extends JpaRepository<Students, Integer> {
    Optional<Students> findByStudentNumber(String studentNumber);
    boolean existsByStudentNumber(String studentNumber);
    
}
