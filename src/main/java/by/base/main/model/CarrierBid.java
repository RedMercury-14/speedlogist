package by.base.main.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "carrier_bid")
public class CarrierBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcarrier_bid")
    private Long idCarrierBid;

    @ManyToOne
    @JoinColumn(name = "carrier_iduser", nullable = false)
    @JsonBackReference
    private User carrier;

    @Column(name = "price")
    private Double price;

    @Column(name = "date_time")
    private Timestamp dateTime;

    @ManyToMany(fetch = FetchType.LAZY,
//			cascade = { CascadeType.ALL }
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    @JoinTable(name = "route_has_carrier_bid", joinColumns = @JoinColumn(name = "idcarrier_bid"), inverseJoinColumns = @JoinColumn(name = "route_idroute"))
//	@JsonBackReference // ТУТ БЫЛО ВКЛЮЧЕНО!!!!
    @JsonIgnore
    private Set<Route> orders;



    @Column(name = "winner")
    private Boolean winner;

    public Long getIdCarrierBid() {
        return idCarrierBid;
    }

    public void setIdCarrierBid(Long idCarrierBid) {
        this.idCarrierBid = idCarrierBid;
    }

    public User getCarrier() {
        return carrier;
    }

    public void setCarrier(User carrier) {
        this.carrier = carrier;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public Set<Route> getOrders() {
        return orders;
    }

    public void setOrders(Set<Route> orders) {
        this.orders = orders;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }
}
