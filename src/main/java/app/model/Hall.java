package app.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Set;

//clasa Sala
@Entity
@JsonSerialize
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idHall"
)
public class Hall {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idHall;

    private Long nrSeats;

    @JsonIgnoreProperties
    @OneToMany(fetch = FetchType.LAZY, mappedBy="hall")
    private Set<Sale> sales;

    @JsonIgnoreProperties
    @OneToMany(fetch = FetchType.LAZY, mappedBy="hall")
    private Set<Concert> concerts;

    public Hall() {
    }

    public Long getIdHall() {
        return idHall;
    }

    public void setIdHall(Long idHall) {
        this.idHall = idHall;
    }

    public Long getNrSeats() {
        return nrSeats;
    }

    public void setNrSeats(Long nrSeats) {
        this.nrSeats = nrSeats;
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy="hall")
    public Set<Sale> getSales() {
        return sales;
    }

    public void setSales(Set<Sale> sales) {
        this.sales = sales;
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy="hall")
    public Set<Concert> getConcerts() {
        return concerts;
    }

    public void setConcerts(Set<Concert> concerts) {
        this.concerts = concerts;
    }
}
