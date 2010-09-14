//$Id: FinancialAsset.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.inheritance.joined;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class FinancialAsset extends Asset {
	private double price;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
