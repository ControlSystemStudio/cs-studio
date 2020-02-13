//$Id: A.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.cid;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;


/**
 * @author Artur Legan
 *
 */
@Entity
public class A implements Serializable{

	@EmbeddedId
	private AId aId;

	public AId getAId() {
		return aId;
	}

	public void setAId(AId aId) {
		this.aId = aId;
	}
}
