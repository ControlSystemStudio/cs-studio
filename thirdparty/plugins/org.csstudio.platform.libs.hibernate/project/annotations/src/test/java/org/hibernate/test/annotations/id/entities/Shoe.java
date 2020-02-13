//$Id: Shoe.java 17734 2009-10-14 04:18:35Z stliu $
package org.hibernate.test.annotations.id.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * sample of Sequance generator
 *
 * @author Emmanuel Bernard
 */
@Entity
@SuppressWarnings("serial")
public class Shoe implements Serializable {
	private Long id;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long long1) {
		id = long1;
	}
}
