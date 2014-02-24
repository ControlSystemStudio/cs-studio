//$Id: Sweater.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.inheritance.joined;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author Emmanuel Bernard
 */
@Entity
@PrimaryKeyJoinColumn(name = "clothing_id")
public class Sweater extends Clothing {
	private boolean isSweat;

	public boolean isSweat() {
		return isSweat;
	}

	public void setSweat(boolean sweat) {
		isSweat = sweat;
	}
}
