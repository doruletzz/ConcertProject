package app.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.Set;

//Clasa Spectacol
@Entity
@Component
@JsonSerialize
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idShow"
)
public class Concert {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idShow;
    private Date dateShow;
    private BigDecimal price;
    private String title;
    private BigDecimal totalAmount;

    @JsonIgnoreProperties
    @OneToMany(fetch = FetchType.LAZY, mappedBy="concert")
    private Set<Sale> sales;

    public Concert() {
    }

//    @JsonBackReference
    @JsonIgnoreProperties
    @ManyToOne
    private Hall hall;

    public Long getIdShow() {
        return idShow;
    }

    public void setIdShow(Long idShow) {
        this.idShow = idShow;
    }

    public Date getDateShow() {
        return dateShow;
    }

    public void setDateShow(Date dateShow) {
        this.dateShow = dateShow;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal price) {
        this.totalAmount = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnoreProperties
    @OneToMany(fetch = FetchType.LAZY, mappedBy="concert")
    public Set<Sale> getSales() {
        return sales;
    }

    public void setSales(Set<Sale> sales) {
        this.sales = sales;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
