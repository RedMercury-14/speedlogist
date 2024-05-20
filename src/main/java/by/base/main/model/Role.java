package by.base.main.model;

import java.util.List;

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
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonBackReference;

@SuppressWarnings("serial")
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority{

	/**
	 * @author Dima Hrushevski
	 */	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "idrole")
	private int idRole;
	
	@Column(name = "roles")
	private String authority;
	
	@Column(name = "description")
	private String description;
	
	@ManyToMany(fetch = FetchType.LAZY, 
				cascade= {CascadeType.PERSIST, CascadeType.MERGE,
					 CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "role_has_user", 
			joinColumns = @JoinColumn(name = "role_idrole"), 
			inverseJoinColumns = @JoinColumn(name = "user_iduser"))
	@JsonBackReference
	private List<User> users;
	
	public Role() {
	}
	
	

	public Role(int id, String role) {
		this.idRole = id;
		this.authority = role;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public int getIdRole() {
		return idRole;
	}

	public void setIdRole(int id) {
		this.idRole = id;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setAuthority(String role) {
		this.authority = role;
	}

	public String getAuthority() {
		return authority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idRole;
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
		Role other = (Role) obj;
		if (idRole != other.idRole)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Role [id=" + idRole + ", role=" + authority + "]";
	}

}
