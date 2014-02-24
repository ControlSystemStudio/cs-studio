//$Id: Dog.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.emops;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public class Dog extends Pet {
	private int numBones;

	public int getNumBones() {
		return numBones;
	}

	public void setNumBones(int numBones) {
		this.numBones = numBones;
	}
}
