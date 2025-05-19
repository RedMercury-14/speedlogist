package by.base.main.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

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
    
    @Column(name = "route_direction")
    private String routeDirection;

    @Column(name = "logist_comment")
    private String logistComment;

    @Column(name = "status")
    private Integer status;

    @Column(name = "user_has_change")
    private String userHasChange;

    @Column(name = "datetime_change")
    private Timestamp datetimeChange;

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

    public String getRouteDirection() {
		return routeDirection;
	}

	public void setRouteDirection(String routeDirection) {
		this.routeDirection = routeDirection;
	}

	public String getLogistComment() {
		return logistComment;
	}

	public void setLogistComment(String logistComment) {
		this.logistComment = logistComment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUserHasChange() {
		return userHasChange;
	}

	public void setUserHasChange(String userHasChange) {
		this.userHasChange = userHasChange;
	}

	public Timestamp getDatetimeChange() {
		return datetimeChange;
	}

	public void setDatetimeChange(Timestamp datetimeChange) {
		this.datetimeChange = datetimeChange;
	}

	@Override
	public int hashCode() {
		return Objects.hash(carrier, comment, companyName, currency, dateTime, idCarrierBid, idUser, logistComment,
				percent, price, route, routeDirection, winner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarrierBid other = (CarrierBid) obj;
		return Objects.equals(carrier, other.carrier) && Objects.equals(comment, other.comment)
				&& Objects.equals(companyName, other.companyName) && Objects.equals(currency, other.currency)
				&& Objects.equals(dateTime, other.dateTime) && Objects.equals(idCarrierBid, other.idCarrierBid)
				&& Objects.equals(idUser, other.idUser) && Objects.equals(logistComment, other.logistComment)
				&& Objects.equals(percent, other.percent) && Objects.equals(price, other.price)
				&& Objects.equals(route, other.route) && Objects.equals(routeDirection, other.routeDirection)
				&& Objects.equals(winner, other.winner);
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
