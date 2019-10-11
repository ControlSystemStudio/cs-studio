//$Id: Mammal.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.ops;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Mammal extends Animal {
	private int mamalNbr;

	public int getMamalNbr() {
		return mamalNbr;
	}

	public void setMamalNbr(int mamalNbr) {
		this.mamalNbr = mamalNbr;
	}
}
