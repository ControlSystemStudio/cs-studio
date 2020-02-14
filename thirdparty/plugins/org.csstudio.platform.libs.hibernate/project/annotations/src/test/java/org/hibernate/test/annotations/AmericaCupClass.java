//$Id: AmericaCupClass.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author Emmanuel Bernard
 */
@Entity
@PrimaryKeyJoinColumn(name = "BOAT_ID")
public class AmericaCupClass extends Boat {
	private Country country;

	@ManyToOne()
	@JoinColumn(name = "COUNTRY_ID")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}
