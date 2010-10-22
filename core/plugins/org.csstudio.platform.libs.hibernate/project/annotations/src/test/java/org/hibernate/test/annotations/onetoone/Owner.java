//$Id: Owner.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.onetoone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.CascadeType;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Owner {
	@Id @GeneratedValue private Integer id;

	@OneToOne(cascade = CascadeType.ALL) @PrimaryKeyJoinColumn private OwnerAddress address;

	public OwnerAddress getAddress() {
		return address;
	}

	public void setAddress(OwnerAddress address) {
		this.address = address;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
