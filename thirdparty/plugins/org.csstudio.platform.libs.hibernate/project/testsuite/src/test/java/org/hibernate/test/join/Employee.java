//$Id: Employee.java 4364 2004-08-17 12:10:32Z oneovthafew $
package org.hibernate.test.join;

import java.math.BigDecimal;

/**
 * @author Gavin King
 */
public class Employee extends Person {
	private String title;
	private BigDecimal salary;
	private Employee manager;
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return Returns the manager.
	 */
	public Employee getManager() {
		return manager;
	}
	/**
	 * @param manager The manager to set.
	 */
	public void setManager(Employee manager) {
		this.manager = manager;
	}
	/**
	 * @return Returns the salary.
	 */
	public BigDecimal getSalary() {
		return salary;
	}
	/**
	 * @param salary The salary to set.
	 */
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
}
