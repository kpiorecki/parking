package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Email;

@Entity
@Cacheable
@Table(name = "users")
public class User extends ArchivableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String login;

	@Column
	private String firstName;

	@Column
	private String lastName;

	@Email
	@Column(nullable = false)
	private String email;

	@Version
	private Integer version;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
