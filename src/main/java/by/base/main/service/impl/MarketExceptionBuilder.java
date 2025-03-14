package by.base.main.service.impl;

public class MarketExceptionBuilder {

	private String message;
    private String status;
    private String responseCode;
    private String description;
    private String request;

    public MarketExceptionBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public MarketExceptionBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public MarketExceptionBuilder setResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public MarketExceptionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public MarketExceptionBuilder setRequest(String request) {
        this.request = request;
        return this;
    }

    public MarketException build() {
        return new MarketException(message, status, responseCode, description, request);
    }
}
