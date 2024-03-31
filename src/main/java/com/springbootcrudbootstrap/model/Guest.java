package com.springbootcrudbootstrap.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;

@Entity
@Table(name = "guest")
public class Guest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3986055557800361972L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@NotFound
	@Column(name = "name")
	private String name;
	@NotFound
	@Column(name = "address")
	private String address;
	@NotFound
	@Column(name = "employment")
	private String employment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmployment() {
		return employment;
	}

	public void setEmployment(String employment) {
		this.employment = employment;
	}

	public Guest() {
		super();
		// TODO Auto-generated constructor stub
	}
 
}
