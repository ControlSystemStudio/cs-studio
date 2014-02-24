//$Id: WomanPk.java 16344 2009-04-15 18:40:43Z gbadner $
package org.hibernate.test.annotations.manytomany;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;

/**
 * @author Emmanuel Bernard
 */
@Embeddable
public class WomanPk implements Serializable {


	private String firstName;
	private String lastName;

	public int hashCode() {
		//this implem sucks
		return getFirstName().hashCode() + getLastName().hashCode();
	}

	public boolean equals(Object obj) {
		//firstName and lastName are expected to be set in this implem
		if ( obj != null && obj instanceof WomanPk ) {
			WomanPk other = (WomanPk) obj;
			return getFirstName().equals( other.getFirstName() )
					&& getLastName().equals( other.getLastName() );
		}
		else {
			return false;
		}
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(length=128)
	public String getFirstName() {
		return firstName;
	}

	@Column(length=128)
	public String getLastName() {
		return lastName;
	}
}
