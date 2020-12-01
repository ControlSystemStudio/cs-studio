//$Id: Decorate.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.emops;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Decorate implements java.io.Serializable {

	private int id;

	private String name;

	private Pet pet;

	public Decorate() {
		super();

	}

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@OneToOne( fetch = FetchType.LAZY )
	public Pet getPet() {
		return pet;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}
}
