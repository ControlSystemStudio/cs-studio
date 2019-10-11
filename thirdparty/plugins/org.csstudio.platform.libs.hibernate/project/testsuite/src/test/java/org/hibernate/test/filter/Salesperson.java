// $Id: Salesperson.java 4448 2004-08-28 02:29:05Z steveebersole $
package org.hibernate.test.filter;

import java.util.Set;
import java.util.HashSet;
import java.util.Date;

/**
 * Implementation of Salesperson.
 * 
 * @author Steve
 */
public class Salesperson {
	private Long id;
	private String name;
	private String region;
	private Date hireDate;
	private Department department;
	private Set orders = new HashSet();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Set getOrders() {
		return orders;
	}

	public void setOrders(Set orders) {
		this.orders = orders;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}
}
