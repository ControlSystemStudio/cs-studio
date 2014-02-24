//$Id: Country.java 18211 2009-12-11 19:14:01Z hardy.ferentschik $
package org.hibernate.test.annotations.embedded;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.AccessType;

/**
 * Non realistic embedded dependent object
 *
 * @author Emmanuel Bernard
 */
@Embeddable
@AccessType("property")
public class Country implements Serializable {
	private String iso2;
	private String name;

	public String getIso2() {
		return iso2;
	}

	public void setIso2(String iso2) {
		this.iso2 = iso2;
	}

	@Column(name = "countryName")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
