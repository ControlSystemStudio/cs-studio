//$Id: Money1.java 18259 2009-12-17 15:34:04Z epbernard $
package org.hibernate.ejb.test.pack.defaultpar_1_0;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Money1 {
	private Integer id;

	@Id @GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}