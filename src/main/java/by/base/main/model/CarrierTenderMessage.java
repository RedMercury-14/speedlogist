package by.base.main.model;

public class CarrierTenderMessage extends Message {

    private CarrierBid carrierBid;
    private Route route;

    public CarrierBid getCarrierBid() {
        return carrierBid;
    }

    public void setCarrierBid(CarrierBid carrierBid) {
        this.carrierBid = carrierBid;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
