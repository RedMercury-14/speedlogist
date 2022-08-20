package by.base.main.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class User implements Serializable{

	/**
	 * @author Dima Hrushevski
	 */
	private static final long serialVersionUID = 4985936175383040476L;
	
	
	
	public User(String login, String password) {
		super();
		this.login = login;
		this.password = password;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "iduser")
	private int idUser;
	
	@Column(name = "login")
	private String login;
	
	@Column(name = "password")
	@JsonIgnore
	private String password;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "surname")
	private String surname;
	
	@Column(name = "patronymic")
	private String patronymic;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "telephone")
	private String telephone;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "enablet")
	private boolean enablet; 
	
	@Column(name = "department")
	private String department;
	
	@Column(name = "email")
	private String eMail;
	
	@ManyToMany(fetch = FetchType.EAGER,
			cascade= {CascadeType.PERSIST, CascadeType.MERGE,
					 CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "role_has_user", 
				joinColumns = @JoinColumn(name = "user_iduser"), 
				inverseJoinColumns = @JoinColumn(name = "role_idrole"))	
	private Set<Role> roles;
	
	@OneToMany(fetch=FetchType.EAGER,
			   mappedBy="user",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	private Set<Truck> trucks;
	
	@OneToMany(fetch=FetchType.EAGER,
			   mappedBy="user",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JsonBackReference
	private Set<Route> route;
	
	@OneToOne(mappedBy="driver", //маршрут привязанный к водителю!
			cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
						CascadeType.REFRESH})
	@JsonBackReference
	private Route singleRoute;	
	
	@OneToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH})
	@JoinColumn(name="shop_numshop")
	@JsonBackReference
	private Shop shop;
	
	@Column(name = "numpass")
	private String numPass;
	
	@Column(name = "numdrivercard")
	private String numDriverCard;
	
	@Column(name = "numYNP")
	private String numYNP;
	
	@Column(name = "numcontract")
	private String numContract;
	
	@Column(name = "loyalty")
	private String loyalty;
	
	@Column(name = "isdriver")
	private boolean isDriver;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "`check`")
	private String check;
	
	@Column(name = "rate")
	private String rate;
	
	@OneToMany(fetch=FetchType.EAGER,
			   mappedBy="user",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JsonBackReference
	private List<Feedback> feedbackList;
	
	@OneToMany(fetch=FetchType.EAGER,
			   mappedBy="user",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JsonBackReference
	private Set<Feedback> from;
	
	@Transient
	private String confirmPassword;
	
	public User(){}

	public User(String login, String password, String name, String surname, String patronymic, String companyName,
			String telephone, String address, boolean enablet, String departament) {
		this.login = login;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.patronymic = patronymic;
		this.companyName = companyName;
		this.telephone = telephone;
		this.address = address;
		this.enablet = enablet;
		this.department = departament;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPatronymic() {
		return patronymic;
	}

	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isEnablet() {
		return enablet;
	}

	public void setEnablet(boolean enablet) {
		this.enablet = enablet;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String departament) {
		this.department = departament;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	

	public String getNumPass() {
		return numPass;
	}

	public void setNumPass(String numPass) {
		this.numPass = numPass;
	}

	public String getNumDriverCard() {
		return numDriverCard;
	}

	public void setNumDriverCard(String numDriverCard) {
		this.numDriverCard = numDriverCard;
	}

	public String getNumYNP() {
		return numYNP;
	}

	public void setNumYNP(String numYNP) {
		this.numYNP = numYNP;
	}

	public String getNumContract() {
		return numContract;
	}

	public void setNumContract(String numContract) {
		this.numContract = numContract;
	}

	public String getLoyalty() {
		return loyalty;
	}

	public void setLoyalty(String loyalty) {
		this.loyalty = loyalty;
	}

	public boolean getIsDriver() {
		return isDriver;
	}

	public void setIsDriver(boolean isDriver) {
		this.isDriver = isDriver;
	}
	
	public Set<Truck> getTrucks() {
		return trucks;
	}

	public void setTrucks(Set<Truck> trucks) {
		this.trucks = trucks;
	}

	public Set<Route> getRoute() {
		return route;
	}

	public void setRoute(Set<Route> route) {
		this.route = route;
	}	

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}
	
	public Route getSingleRoute() {
		return singleRoute;
	}

	public void setSingleRoute(Route singleRoute) {
		this.singleRoute = singleRoute;
	}

	public List<Feedback> getFeedbackList() {
		return feedbackList;
	}

	public void setFeedbackList(List<Feedback> feedbackList) {
		this.feedbackList = feedbackList;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idUser);
	}
	
	public Set<Feedback> getFrom() {
		return from;
	}

	public void setFrom(Set<Feedback> from) {
		this.from = from;
	}
	
	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}
	
	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return idUser == other.idUser;
	}

	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", login=" + login + ", name=" + name + ", surname=" + surname
				+ ", patronymic=" + patronymic + ", companyName=" + companyName + ", telephone=" + telephone
				+ ", address=" + address + ", enablet=" + enablet + ", department=" + department + ", eMail=" + eMail
				+ ", roles=" + roles + "]";
	}
}