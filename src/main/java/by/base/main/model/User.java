package by.base.main.model;

import java.io.Serializable;
import java.sql.Date;
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

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
	
	@Column(name = "director")
	private String director;
	
	@ManyToMany(fetch = FetchType.EAGER,
			cascade= {CascadeType.PERSIST, CascadeType.MERGE,
					 CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "role_has_user", 
				joinColumns = @JoinColumn(name = "user_iduser"), 
				inverseJoinColumns = @JoinColumn(name = "role_idrole"))	
	private Set<Role> roles;
	
	@OneToMany(fetch=FetchType.LAZY,
			   mappedBy="user",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JsonBackReference
	private Set<Truck> trucks;
	
	@OneToMany(fetch=FetchType.LAZY,
			   mappedBy="user",
			   cascade= {CascadeType.ALL}, orphanRemoval = true)
	@JsonBackReference
	private Set<Route> route;
	
	@OneToOne(fetch=FetchType.LAZY,
			mappedBy="driver", //маршрут привязанный к водителю!
			cascade={CascadeType.ALL}, orphanRemoval = true)
	@JsonBackReference
	private Route singleRoute;	
	
	@OneToOne(fetch=FetchType.LAZY,
			cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH})
	@JoinColumn(name="shop_numshop")
	@JsonBackReference
	private Shop shop;
	
	@Transient
	private String seriesAndNumberPass;
	@Transient
	private String issuedBy;
	@Transient
	private String validityPass;
	@Transient
	private String personalNumberPass;
	
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
	
	@Column(name = "requisites")
	private String requisites;
	
	@OneToMany(fetch=FetchType.LAZY,
			   mappedBy="user",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JsonBackReference
	private Set<Feedback> feedbackList;
	
	/**
	 * направление перевозок (разделитель ; )\nи Иные зарубежные страны
	 */
	@Column(name = "direction_of_transportation")
	private String directionOfTransportation;
	
	/**
	 * Форма собственности (ООО, УП, ОАО)
	 */
	@Column(name = "property_size")
	private String propertySize;
	
	@Column(name = "country_of_registration")
	private String countryOfRegistration;
	
	/**
	 * Свидетельство о регистрации (серия / номер/ дата/)
	 */
	@Column(name = "registration_certificate")
	private String registrationCertificate;
	
	/**
	 * дочерние компании (если нет то NULL)
	 */
	@Column(name = "affiliated_companies")
	private String affiliatedCompanies;
	
	@Column(name = "isTIR")
	private Boolean TIR;
	
	/**
	 * Характеристика подвижного состава:\nТентовые сцепки\nРефрижераторы\nТермофургоны\nКонтейнерные площадки 20 футов\nКонтейнерные площадки 40 футов\nКонтейнерные площадки 45 футов\n
	 */
	@Column(name = "characteristics_of_truks")
	private String characteristicsOfTruks;
	
	/**
	 * Количество единиц подвижного состава\n
	 */
	@Column(name = "number_of_truks")
	private Integer numberOfTruks;	
	
	@Column(name = "block")
	private Boolean block;	
	
	@Column(name = "ip")
	private String ip;	
	
	@Column(name = "search")
	private String search;	
	
	/**
	 * Дата регистрации
	 */
	@Column(name = "date_registration")
	private Date dateRegistration;	
	
	@Transient
	private String confirmPassword;
	
	@Transient
	@JsonIgnore // закрыл потому что ломает запрос от Route к User 
	private int numCar;
	
	
	
	public User(){		
	}

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

	public Set<Feedback> getFeedbackList() {
		return feedbackList;
	}

	public void setFeedbackList(Set<Feedback> feedbackList) {
		this.feedbackList = feedbackList;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idUser);
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
	

	public String getRequisites() {
		return requisites;
	}

	public void setRequisites(String requisites) {
		this.requisites = requisites;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}
	
	

	public String getSeriesAndNumberPass() {
		return seriesAndNumberPass;
	}

	public void setSeriesAndNumberPass(String seriesAndNumberPass) {
		this.seriesAndNumberPass = seriesAndNumberPass;
		if (personalNumberPass != null && validityPass != null && issuedBy!= null && this.seriesAndNumberPass != null) {
			this.numPass = seriesAndNumberPass + "; выдан" + issuedBy + " " + validityPass + "; " + personalNumberPass;
		}
	}

	public String getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
		if (personalNumberPass != null && validityPass != null && this.issuedBy!= null && seriesAndNumberPass != null) {
			this.numPass = seriesAndNumberPass + "; выдан " + issuedBy + " " + validityPass + "; " + personalNumberPass;
		}
	}

	public String getValidityPass() {
		return validityPass;
	}

	public void setValidityPass(String validityPass) {
		this.validityPass = validityPass;
		if (personalNumberPass != null && this.validityPass != null && issuedBy!= null && seriesAndNumberPass != null) {
			this.numPass = seriesAndNumberPass + "; выдан" + issuedBy + " " + validityPass + "; " + personalNumberPass;
		}
	}

	public String getPersonalNumberPass() {
		return personalNumberPass;
	}

	public void setPersonalNumberPass(String personalNumberPass) {
		this.personalNumberPass = personalNumberPass;		
		if (this.personalNumberPass != null && validityPass != null && issuedBy!= null && seriesAndNumberPass != null) {
			this.numPass = seriesAndNumberPass + "; выдан" + issuedBy + " " + validityPass + "; " + personalNumberPass;
		}
				
	}

	public String getDirectionOfTransportation() {
		return directionOfTransportation;
	}

	public void setDirectionOfTransportation(String directionOfTransportation) {
		this.directionOfTransportation = directionOfTransportation;
	}

	public String getPropertySize() {
		return propertySize;
	}

	public void setPropertySize(String propertySize) {
		this.propertySize = propertySize;
	}

	public String getCountryOfRegistration() {
		return countryOfRegistration;
	}

	public void setCountryOfRegistration(String countryOfRegistration) {
		this.countryOfRegistration = countryOfRegistration;
	}

	public String getRegistrationCertificate() {
		return registrationCertificate;
	}

	public void setRegistrationCertificate(String registrationCertificate) {
		this.registrationCertificate = registrationCertificate;
	}

	public String getAffiliatedCompanies() {
		return affiliatedCompanies;
	}

	public void setAffiliatedCompanies(String affiliatedCompanies) {
		this.affiliatedCompanies = affiliatedCompanies;
	}

	public Boolean getTIR() {
		return TIR;
	}
	
	public Boolean isTIR() {
		return TIR;
	}

	public void setTIR(Boolean tIR) {
		TIR = tIR;
	}

	public String getCharacteristicsOfTruks() {
		return characteristicsOfTruks;
	}

	public void setCharacteristicsOfTruks(String characteristicsOfTruks) {
		this.characteristicsOfTruks = characteristicsOfTruks;
	}

	public Integer getNumberOfTruks() {
		return numberOfTruks;
	}

	public void setNumberOfTruks(Integer numberOfTruks) {
		this.numberOfTruks = numberOfTruks;
	}

	public Boolean getBlock() {
		return block;
	}
	
	public Boolean isBlock() {
		return block;
	}

	public void setBlock(Boolean block) {
		this.block = block;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Date getDateRegistration() {
		return dateRegistration;
	}

	public void setDateRegistration(Date dateRegistration) {
		this.dateRegistration = dateRegistration;
	}
	
	public int getNumCar() {
		//задаём общее число машин, если это перевозчик
				Role carrer = new Role(7, "ROLE_CARRIER");
				if(roles != null && roles.contains(carrer)) {
					numCar = trucks != null ? trucks.size() : 0;
				}
		return numCar;
	}

	public void setNumCar(int numCar) {
		this.numCar = numCar;
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
				+ "]";
	}
}