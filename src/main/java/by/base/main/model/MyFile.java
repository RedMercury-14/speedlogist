package by.base.main.model;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "files")
public class MyFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfiles")
    private Long idFiles;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "date_create")
    private Timestamp dateCreate;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name", columnDefinition = "TEXT")
    private String userName;

    @Column(name = "user_company_name", columnDefinition = "TEXT")
    private String userCompanyName;

    @Column(name = "user_email", columnDefinition = "TEXT")
    private String userEmail;

    @Column(name = "status")
    private Integer status;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "size", columnDefinition = "TEXT")
    private Double size;
    
    @Column(name = "size_type", columnDefinition = "TEXT")
    private String sizeType;
    
    @Column(name = "type", columnDefinition = "TEXT")
    private String type;
    
    @Column(name = "application", columnDefinition = "TEXT")
    private String application;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "id_route")
    private Integer idRoute;
    
    @Column(name = "id_order")
    private Integer idOrder;
    
    @Column(name = "id_object")
    private Long idObject;

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSizeType() {
		return sizeType;
	}

	public void setSizeType(String sizeType) {
		this.sizeType = sizeType;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Integer getIdRoute() {
		return idRoute;
	}

	public void setIdRoute(Integer idRoute) {
		this.idRoute = idRoute;
	}

	public Integer getIdOrder() {
		return idOrder;
	}

	public void setIdOrder(Integer idOrder) {
		this.idOrder = idOrder;
	}

	public Long getIdObject() {
		return idObject;
	}

	public void setIdObject(Long idObject) {
		this.idObject = idObject;
	}

	public Long getIdFiles() {
		return idFiles;
	}

	public void setIdFiles(Long idFiles) {
		this.idFiles = idFiles;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Timestamp getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Timestamp dateCreate) {
		this.dateCreate = dateCreate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserCompanyName() {
		return userCompanyName;
	}

	public void setUserCompanyName(String userCompanyName) {
		this.userCompanyName = userCompanyName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + Objects.hash(comment, contentType, dateCreate, fileName, idFiles, status, userCompanyName,
				userEmail, userId, userName);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyFile other = (MyFile) obj;
		return Objects.equals(comment, other.comment) && Objects.equals(contentType, other.contentType)
				&& Arrays.equals(data, other.data) && Objects.equals(dateCreate, other.dateCreate)
				&& Objects.equals(fileName, other.fileName) && Objects.equals(idFiles, other.idFiles)
				&& Objects.equals(status, other.status) && Objects.equals(userCompanyName, other.userCompanyName)
				&& Objects.equals(userEmail, other.userEmail) && Objects.equals(userId, other.userId)
				&& Objects.equals(userName, other.userName);
	}

	@Override
	public String toString() {
		return "File [idFiles=" + idFiles + ", fileName=" + fileName + ", contentType=" + contentType + ", data="
				+ Arrays.toString(data) + ", dateCreate=" + dateCreate + ", userId=" + userId + ", userName=" + userName
				+ ", userCompanyName=" + userCompanyName + ", userEmail=" + userEmail + ", status=" + status
				+ ", comment=" + comment + "]";
	}
    
    
}
