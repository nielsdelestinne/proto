package be.niedel.proto.employmentservice.employer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerRepository extends JpaRepository<Employer, String> {
}
