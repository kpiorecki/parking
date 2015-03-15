package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.joda.time.DateTime;

@Entity
@Table(name = "booking_entries", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_fk", "booking_fk" }) })
public class BookingEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_booking_entries")
	@TableGenerator(name = "seq_booking_entries", pkColumnValue = "seq_booking_entries")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_fk")
	private User user;

	@Column(nullable = false, updatable = false)
	private DateTime creationTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	@PrePersist
	protected void prePersist() {
		this.creationTime = new DateTime();
	}

}
