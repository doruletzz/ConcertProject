package app.repos;

import app.model.Concert;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Configuration
@Component
public interface ConcertRepository extends JpaRepository<Concert, Long> {

}