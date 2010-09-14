//$Id: GenerationGroup.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.indexcoll;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class GenerationGroup {

	@Id
	@GeneratedValue
	private int id;

	private Generation generation;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Generation getGeneration() {
		return generation;
	}

	public void setGeneration(Generation generation) {
		this.generation = generation;
	}


}