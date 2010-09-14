//$Id: Training.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.indexcoll;

import java.util.SortedMap;
import java.util.TreeMap;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Training {
	@Id @GeneratedValue private Long id;
	@Sort(type= SortType.NATURAL)
	@MapKey(name="name") @ManyToMany SortedMap<String, Trainee> trainees = new TreeMap<String, Trainee>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SortedMap<String, Trainee> getTrainees() {
		return trainees;
	}

	public void setTrainees(SortedMap<String, Trainee> trainees) {
		this.trainees = trainees;
	}
}
