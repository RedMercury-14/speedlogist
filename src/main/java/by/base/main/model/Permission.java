package by.base.main.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "permissions")
public class Permission {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpermissions")
    private int idPermissions; // Первичный ключ таблицы

    @Column(name = "name_method", length = 255)
    private String nameMethod; // Короткое название метода, к которому относится разрешение, например слоты или пересток или др.

    @Column(name = "user_initiator", columnDefinition = "TEXT")
    private String userInitiator; // Непосредственно инициатор разрешения, кому требуется разрешение

    @Column(name = "id_user_initiator")
    private Integer idUserInitiator; // id Непосредственно инициатор разрешения, кому требуется разрешение

    @Column(name = "name_user_initiator", columnDefinition = "TEXT")
    private String nameUserInitiator; // Полное имя кому требуется разрешение

    @Column(name = "email_user_initiator", columnDefinition = "TEXT")
    private String emailUserInitiator; // Email кому требуется разрешение

    @Column(name = "tel_user_initiator", length = 255)
    private String telUserInitiator; // Телефон кому требуется разрешение

    @Column(name = "date_time_initiations")
    private Timestamp dateTimeInitiations; // Дата и время, когда был создан запрос

    @Column(name = "user_approver", columnDefinition = "TEXT")
    private String userApprover; // Юзер, который вынес решение по этому разрешению

    @Column(name = "id_user_approver")
    private Integer idUserApprover; // id Юзера, который вынес решение по этому разрешению

    @Column(name = "name_user_approver", columnDefinition = "TEXT")
    private String nameUserApprover; // Полное имя юзера, который вынес решение по этому разрешению

    @Column(name = "email_user_approver", columnDefinition = "TEXT")
    private String emailUserApprover; // Email юзера, который вынес решение по этому разрешению

    @Column(name = "tel_user_approver", length = 255)
    private String telUserApprover; // Телефон юзера, который вынес решение по этому разрешению

    @Column(name = "status_approval")
    private Boolean statusApproval; // Булевый статус. Если true - можно, если false - нельзя, если null - то еще не обработано

    @Column(name = "date_time_approval")
    private Timestamp dateTimeApproval; // Дата и время внесения решения

    @Column(name = "status")
    private Integer status; // Общий статус

    @Column(name = "comment_user_initiator", columnDefinition = "TEXT")
    private String commentUserInitiator; // Комментарий инициатора

    @Column(name = "comment_user_approver", columnDefinition = "TEXT")
    private String commentUserApprover; // Комментарий утверждающего

    @Column(name = "history", columnDefinition = "TEXT")
    private String history; // История изменений

    @Column(name = "id_object_approver")
    private Integer idObjectApprover; // Непосредственно объект, который требует подтверждения (order или др.)

    @Column(name = "date_valid")
    private Date dateValid; // Дата, на которую действует это разрешение. Например, если это слот, то эта дата означает, что разрешение действует на определенный слот и именно на эту дату

    @Column(name = "time_valid")
    private Date timeValid; // Время, на которое действует это разрешение

	public int getIdPermissions() {
		return idPermissions;
	}

	public void setIdPermissions(int idPermissions) {
		this.idPermissions = idPermissions;
	}

	public String getNameMethod() {
		return nameMethod;
	}

	public void setNameMethod(String nameMethod) {
		this.nameMethod = nameMethod;
	}

	public String getUserInitiator() {
		return userInitiator;
	}

	/**
	 * Логин, кому требуется разрешение
	 * @param userInitiator
	 */
	public void setUserInitiator(String userInitiator) {
		this.userInitiator = userInitiator;
	}

	public Integer getIdUserInitiator() {
		return idUserInitiator;
	}

	public void setIdUserInitiator(Integer idUserInitiator) {
		this.idUserInitiator = idUserInitiator;
	}

	public String getNameUserInitiator() {
		return nameUserInitiator;
	}

	public void setNameUserInitiator(String nameUserInitiator) {
		this.nameUserInitiator = nameUserInitiator;
	}

	public String getEmailUserInitiator() {
		return emailUserInitiator;
	}

	public void setEmailUserInitiator(String emailUserInitiator) {
		this.emailUserInitiator = emailUserInitiator;
	}

	public String getTelUserInitiator() {
		return telUserInitiator;
	}

	public void setTelUserInitiator(String telUserInitiator) {
		this.telUserInitiator = telUserInitiator;
	}

	public Timestamp getDateTimeInitiations() {
		return dateTimeInitiations;
	}

	public void setDateTimeInitiations(Timestamp dateTimeInitiations) {
		this.dateTimeInitiations = dateTimeInitiations;
	}

	public String getUserApprover() {
		return userApprover;
	}

	public void setUserApprover(String userApprover) {
		this.userApprover = userApprover;
	}

	public Integer getIdUserApprover() {
		return idUserApprover;
	}

	public void setIdUserApprover(Integer idUserApprover) {
		this.idUserApprover = idUserApprover;
	}

	public String getNameUserApprover() {
		return nameUserApprover;
	}

	public void setNameUserApprover(String nameUserApprover) {
		this.nameUserApprover = nameUserApprover;
	}

	public String getEmailUserApprover() {
		return emailUserApprover;
	}

	public void setEmailUserApprover(String emailUserApprover) {
		this.emailUserApprover = emailUserApprover;
	}

	public String getTelUserApprover() {
		return telUserApprover;
	}

	public void setTelUserApprover(String telUserApprover) {
		this.telUserApprover = telUserApprover;
	}

	public Boolean getStatusApproval() {
		return statusApproval;
	}

	public void setStatusApproval(Boolean statusApproval) {
		this.statusApproval = statusApproval;
	}

	public Timestamp getDateTimeApproval() {
		return dateTimeApproval;
	}

	public void setDateTimeApproval(Timestamp dateTimeApproval) {
		this.dateTimeApproval = dateTimeApproval;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCommentUserInitiator() {
		return commentUserInitiator;
	}

	public void setCommentUserInitiator(String commentUserInitiator) {
		this.commentUserInitiator = commentUserInitiator;
	}

	public String getCommentUserApprover() {
		return commentUserApprover;
	}

	public void setCommentUserApprover(String commentUserApprover) {
		this.commentUserApprover = commentUserApprover;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public Integer getIdObjectApprover() {
		return idObjectApprover;
	}

	public void setIdObjectApprover(Integer idObjectApprover) {
		this.idObjectApprover = idObjectApprover;
	}

	public Date getDateValid() {
		return dateValid;
	}

	public void setDateValid(Date dateValid) {
		this.dateValid = dateValid;
	}

	public Date getTimeValid() {
		return timeValid;
	}

	public void setTimeValid(Date timeValid) {
		this.timeValid = timeValid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(commentUserApprover, commentUserInitiator, dateTimeApproval, dateTimeInitiations, dateValid,
				emailUserApprover, emailUserInitiator, history, idObjectApprover, idPermissions, idUserApprover,
				idUserInitiator, nameMethod, nameUserApprover, nameUserInitiator, status, statusApproval,
				telUserApprover, telUserInitiator, timeValid, userApprover, userInitiator);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permission other = (Permission) obj;
		return Objects.equals(commentUserApprover, other.commentUserApprover)
				&& Objects.equals(commentUserInitiator, other.commentUserInitiator)
				&& Objects.equals(dateTimeApproval, other.dateTimeApproval)
				&& Objects.equals(dateTimeInitiations, other.dateTimeInitiations)
				&& Objects.equals(dateValid, other.dateValid)
				&& Objects.equals(emailUserApprover, other.emailUserApprover)
				&& Objects.equals(emailUserInitiator, other.emailUserInitiator)
				&& Objects.equals(history, other.history) && Objects.equals(idObjectApprover, other.idObjectApprover)
				&& idPermissions == other.idPermissions && Objects.equals(idUserApprover, other.idUserApprover)
				&& Objects.equals(idUserInitiator, other.idUserInitiator)
				&& Objects.equals(nameMethod, other.nameMethod)
				&& Objects.equals(nameUserApprover, other.nameUserApprover)
				&& Objects.equals(nameUserInitiator, other.nameUserInitiator) && Objects.equals(status, other.status)
				&& Objects.equals(statusApproval, other.statusApproval)
				&& Objects.equals(telUserApprover, other.telUserApprover)
				&& Objects.equals(telUserInitiator, other.telUserInitiator)
				&& Objects.equals(timeValid, other.timeValid) && Objects.equals(userApprover, other.userApprover)
				&& Objects.equals(userInitiator, other.userInitiator);
	}

	@Override
	public String toString() {
		return "Permission [idPermissions=" + idPermissions + ", nameMethod=" + nameMethod + ", userInitiator="
				+ userInitiator + ", idUserInitiator=" + idUserInitiator + ", nameUserInitiator=" + nameUserInitiator
				+ ", emailUserInitiator=" + emailUserInitiator + ", telUserInitiator=" + telUserInitiator
				+ ", dateTimeInitiations=" + dateTimeInitiations + ", userApprover=" + userApprover
				+ ", idUserApprover=" + idUserApprover + ", nameUserApprover=" + nameUserApprover
				+ ", emailUserApprover=" + emailUserApprover + ", telUserApprover=" + telUserApprover
				+ ", statusApproval=" + statusApproval + ", dateTimeApproval=" + dateTimeApproval + ", status=" + status
				+ ", commentUserInitiator=" + commentUserInitiator + ", commentUserApprover=" + commentUserApprover
				+ ", history=" + history + ", idObjectApprover=" + idObjectApprover + ", dateValid=" + dateValid
				+ ", timeValid=" + timeValid + "]";
	}
    
    
}
