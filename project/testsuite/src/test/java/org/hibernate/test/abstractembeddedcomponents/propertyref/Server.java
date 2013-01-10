package org.hibernate.test.abstractembeddedcomponents.propertyref;

import org.hibernate.test.abstractembeddedcomponents.propertyref.Address;

/**
 * @author Steve Ebersole
 */
public interface Server {
	public Long getId();
	public void setId(Long id);
	public String getServerType();
	public void setServerType(String serverType);
	public Address getAddress();
	public void setAddress(Address address);
}
