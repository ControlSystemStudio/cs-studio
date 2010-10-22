//$Id: SocialSecurityPhysicalAccount.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.reflection;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class SocialSecurityPhysicalAccount {
	public String number;
	public String countryCode;
}
