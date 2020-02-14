//$Id: Customer.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.callbacks;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Table(name = "CUSTOMER_TABLE")
@Inheritance(strategy = InheritanceType.JOINED)
public class Customer extends Person {
	private Employee salesperson;
	private String comments;

	@OneToOne
	@JoinColumn(name = "salesperson")
	public Employee getSalesperson() {
		return salesperson;
	}

	public void setSalesperson(Employee salesperson) {
		this.salesperson = salesperson;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
