//$Id: AddressId.java 5686 2005-02-12 07:27:32Z steveebersole $
package org.hibernate.test.typedonetoone;

import java.io.Serializable;

/**
 * @author Gavin King
 */
public class AddressId implements Serializable {
	private String type;
	private String customerId;
	
	public AddressId(String type, String customerId) {
		this.customerId = customerId;
		this.type = type;
	}
	
	public AddressId() {}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public boolean equals(Object other) {
		if ( !(other instanceof AddressId) ) return false;
		AddressId add = (AddressId) other;
		return type.equals(add.type) && customerId.equals(add.customerId);
	}
	public int hashCode() {
		return customerId.hashCode() + type.hashCode();
	}

}
