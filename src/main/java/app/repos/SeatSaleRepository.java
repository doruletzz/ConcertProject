package app.repos;

import app.model.Hall;
import app.model.Sale;
import app.model.SeatSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatSaleRepository  extends JpaRepository<SeatSale, Long> {
    @Query(value = "SELECT s FROM SeatSale s WHERE s.nrSeat IN :seats")
    List<SeatSale> findAllByNrSeats(@Param("seats") Iterable<Long> seats);
}