package app.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@JsonSerialize
@Component
public class SeatSale {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idSeatSale;

    private Long nrSeat;

    @JsonBackReference
    @ManyToOne
    private Sale sale;

    public SeatSale() {
    }

    public Long getIdSeatSale() {
        return idSeatSale;
    }

    public void setIdSeatSale(Long idShowSale) {
        this.idSeatSale = idShowSale;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Long getNrSeat() {
        return nrSeat;
    }

    public void setNrSeat(Long nrSeat) {
        this.nrSeat = nrSeat;
    }
}
