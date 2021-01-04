package app.repos;

import app.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository  extends JpaRepository<Hall, Long> {
}