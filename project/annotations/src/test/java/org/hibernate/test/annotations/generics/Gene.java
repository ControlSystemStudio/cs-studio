package org.hibernate.test.annotations.generics;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

/**
 * @author Paolo Perrotta
 */
@Entity
public class Gene<T, STE extends Enum> {

	private Integer id;
	private STE state;

	@Type(type="org.hibernate.test.annotations.generics.StateType")
	public STE getState() {
		return state;
	}

	public void setState(STE state) {
		this.state = state;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(targetEntity = DNA.class)
	public T getGeneticCode() {
		return null;
	}

	public void setGeneticCode(T gene) {
	}
}
