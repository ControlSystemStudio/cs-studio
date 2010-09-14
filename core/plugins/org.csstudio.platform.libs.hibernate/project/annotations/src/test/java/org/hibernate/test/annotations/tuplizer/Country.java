//$Id: Country.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.tuplizer;

import javax.persistence.Embeddable;
import javax.persistence.Column;

/**
 * @author Emmanuel Bernard
 */
@Embeddable
public interface Country {
	@Column(name = "CountryName")
	public String getName();
	public void setName(String name);
}
