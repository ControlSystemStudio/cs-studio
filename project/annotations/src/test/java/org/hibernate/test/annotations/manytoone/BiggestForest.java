//$Id: BiggestForest.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytoone;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class BiggestForest {
	private Integer id;
	private ForestType type;

	@Id @GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(mappedBy = "biggestRepresentative")
	public ForestType getType() {
		return type;
	}

	public void setType(ForestType type) {
		this.type = type;
	}
}
