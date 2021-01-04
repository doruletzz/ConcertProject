package app.model;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.Set;

//Clasa Vanzare
@Entity
@Component
@JsonSerialize
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idSale"
)
public class Sale {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idSale;


    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy="sale")
    private Set<SeatSale> seatSales;

    @JsonIgnoreProperties
    @ManyToOne(fetch = FetchType.LAZY)
    private Concert concert;

    private Date date;

    private BigDecimal amount;


    public Sale(){
    }

    @JsonBackReference
    @ManyToOne
    private Hall hall;

    public Long getIdSale() {
        return idSale;
    }

    public void setIdSale(Long idSale) {
        this.idSale = idSale;
    }

    public Concert getShow() {
        return concert;
    }

    public void setShow(Concert concert) {
        this.concert = concert;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<SeatSale> getSeatSales() {
        return seatSales;
    }

    public void setSeatSales(Set<SeatSale> seatSales) {
        this.seatSales = seatSales;
    }

    @JsonIgnoreProperties
    @ManyToOne(fetch = FetchType.LAZY)
    public Concert getConcert() {
        return concert;
    }

    public void setConcert(Concert concert) {
        this.concert = concert;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
