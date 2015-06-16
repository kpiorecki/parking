package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Email;

import com.kpiorecki.parking.ejb.dto.UserDto;

@Entity
@Cacheable
@Table(name = "users")
@NamedQuery(name = "User.findLoginCount", query = "select count(u) from User u where u.login = :login")
public class User extends ArchivableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = UserDto.LOGIN_MAX_LEN)
	private String login;

	@Column(length = UserDto.FIRSTNAME_MAX_LEN)
	private String firstName;

	@Column(length = UserDto.LASTNAME_MAX_LEN)
	private String lastName;

	@Email
	@Column(nullable = false, length = UserDto.EMAIL_MAX_LEN)
	private String email;

	@Column(nullable = false, length = 64)
	private String password;

	@ElementCollection
	@CollectionTable(name = "user_groups", joinColumns = @JoinColumn(name = "login"))
	@Column(name = "group_name", nullable = false)
	@Enumerated(EnumType.STRING)
	private Set<UserGroup> groups = new HashSet<>();

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<UserGroup> getGroups() {
		return Collections.unmodifiableSet(groups);
	}

	public void addGroup(UserGroup group) {
		groups.add(group);
	}

	public void removeGroup(UserGroup group) {
		groups.remove(group);
	}

	public void removeAllGroups() {
		groups.clear();
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
