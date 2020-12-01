//$Id: Discount.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


/**
 * Discount ticket a client can use when buying tickets
 *
 * @author Emmanuel Bernard
 */
@Entity
public class Discount implements Serializable {

	private Long id;
	private double discount;
	private Customer owner;


	@Column(precision = 5, scale = 2)
	public double getDiscount() {
		return discount;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setDiscount(double i) {
		discount = i;
	}

	public void setId(Long long1) {
		id = long1;
	}

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID")
	public Customer getOwner() {
		return owner;
	}

	public void setOwner(Customer customer) {
		owner = customer;
	}

}

