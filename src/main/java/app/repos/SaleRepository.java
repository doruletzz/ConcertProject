package app.repos;

import app.model.Concert;
import app.model.Hall;
import app.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleRepository  extends JpaRepository<Sale, Long> {

    @Query(value = "SELECT s FROM Sale s WHERE s.concert = :concert")
    List<Sale> findSalesByConcert(@Param("concert") Concert concert);
}