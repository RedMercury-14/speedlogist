package by.base.main.model;

import java.util.Objects;

public class Message {
	
	private String fromUser;
	private String toUser;
	private String text;
	private String idRoute;
	private String status;
	private String companyName;
	
	
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getIdRoute() {
		return idRoute;
	}
	public void setIdRoute(String idRoute) {
		this.idRoute = idRoute;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	@Override
	public int hashCode() {
		return Objects.hash(fromUser, status, text, toUser);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		return Objects.equals(fromUser, other.fromUser) && Objects.equals(status, other.status)
				&& Objects.equals(text, other.text) && Objects.equals(toUser, other.toUser);
	}
	@Override
	public String toString() {
		return "Message [fromUser=" + fromUser + ", toUser=" + toUser + ", text=" + text + ", idRoute=" + idRoute
				+ ", status=" + status + ", companyName=" + companyName + "]";
	}
	
	
	
	
}
