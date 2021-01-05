package app.repos;

import app.model.CheckData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckDataRepository extends JpaRepository<CheckData, Long> {
}
