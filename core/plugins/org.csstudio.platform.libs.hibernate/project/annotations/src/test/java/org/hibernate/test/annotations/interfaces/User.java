//$Id: User.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.interfaces;

import java.util.Collection;

/**
 * @author Emmanuel Bernard
 */
public interface User {
	Integer getId();

	Collection<Contact> getContacts();


}
