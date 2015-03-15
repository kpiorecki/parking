package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.CacheIndex;
import org.hibernate.validator.constraints.Email;

@Entity
@Cacheable
@Table(name = "users", indexes = { @Index(columnList = "login", unique = true) })
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_users")
	@TableGenerator(name = "seq_users", pkColumnValue = "seq_users")
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	@CacheIndex
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
