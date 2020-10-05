//$Id: Frame.java 16533 2009-05-11 14:10:17Z hardy.ferentschik $
package org.hibernate.test.annotations.manytoone;

import java.util.Set;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Formula;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Frame implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	@OneToMany( mappedBy = "frame" )
	private Set<Lens> lenses;
	private String name;
	@Formula("lower(name)")
	private String lowerName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Lens> getLenses() {
		return lenses;
	}

	public void setLenses(Set<Lens> lenses) {
		this.lenses = lenses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
