//$Id: Reptile.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.ops;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Reptile extends Animal {
	private float temperature;

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
}
