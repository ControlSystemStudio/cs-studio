//$Id: Boat.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


/**
 * Boat class. Mapped in a Joined manner
 *
 * @author Emmanuel Bernard
 */
@Entity()
@Inheritance(
		strategy = InheritanceType.JOINED
)
public class Boat implements Serializable {
	private Integer id;
	private int size;
	private int weight;

	public Boat() {
		super();
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	@Column(name = "boat_size")
	public int getSize() {
		return size;
	}

	public void setId(Integer integer) {
		id = integer;
	}

	public void setSize(int i) {
		size = i;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
