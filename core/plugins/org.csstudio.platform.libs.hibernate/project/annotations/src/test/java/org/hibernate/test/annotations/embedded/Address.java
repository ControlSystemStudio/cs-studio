//$Id: Address.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.embedded;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * @author Emmanuel Bernard
 */
@Embeddable
public class Address implements Serializable {
	String address1;
	@Column(name = "fld_city")
	String city;
	Country country;
	@ManyToOne
	AddressType type;
}
