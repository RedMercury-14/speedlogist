package by.base.main.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "carrier_bid")
public class CarrierBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcarrier_bid")
    private Long idCarrierBid;

    @ManyToOne
    @JoinColumn(name = "carrier_iduser", nullable = false)
    @JsonIgnore
    private User carrier;

    @Column(name = "price")
    private Integer price;

    @Column(name = "date_time")
    private Timestamp dateTime;

    @ManyToOne
    @JoinColumn(name = "route_idroute", nullable = false)
    @JsonBackReference
    private Route route;

    @Column(name = "winner")
    private Boolean winner;

    @Column(name = "percent")
    private Integer percent;

    @Column(name = "currency")
    private String currency;

    @Column(name = "comment")
    private String comment;

    @Column(name = "idUser")
    private Integer idUser;

    @Column(name = "company_name")
    private String companyName;


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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "CarrierBid[" +
                "idCarrierBid=" + idCarrierBid +
                ", price=" + price +
                ", dateTime=" + dateTime +
                ", routeId=" + route.getIdRoute() +
                ", winner=" + winner +
                ", percent=" + percent +
                ", currency='" + currency + '\'' +
                ", comment='" + comment + '\'' +
                ", idUser=" + idUser +
                ']';
    }
}
