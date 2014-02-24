// $Id: A.java 19255 2010-04-21 01:57:44Z steve.ebersole@jboss.com $
package org.hibernate.test.annotations.fkcircularity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Test entities ANN-722.
 * 
 * @author Hardy Ferentschik
 *
 */
@Entity
public class A {
	private A_PK id;

	@EmbeddedId
	public A_PK getId() {
		return id;
	}

	public void setId(A_PK id) {
		this.id = id;
	}
}
