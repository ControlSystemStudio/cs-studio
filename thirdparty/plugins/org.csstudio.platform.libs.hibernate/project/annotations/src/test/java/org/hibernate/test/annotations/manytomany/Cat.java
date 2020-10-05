//$Id: Cat.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytomany;

import java.util.Set;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.Index;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Table(name = "tbl_cat")
//ANN-630
//@org.hibernate.annotations.Table(appliesTo= "TT", indexes = @Index(name = "testidx", columnNames = "cat_id"))
public class Cat {
	private CatPk id;
	private int age;
	private Set<Woman> humanContacts;

	@ManyToMany
	//@Index(name = "CAT_HUMAN_IDX")
	@JoinTable(name="TT")
	public Set<Woman> getHumanContacts() {
		return humanContacts;
	}

	public void setHumanContacts(Set<Woman> humanContacts) {
		this.humanContacts = humanContacts;
	}

	@EmbeddedId()
	public CatPk getId() {
		return id;
	}

	public void setId(CatPk id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean equals(Object o) {
		if ( this == o ) return true;
		if ( !( o instanceof Cat ) ) return false;

		final Cat cat = (Cat) o;

		if ( !id.equals( cat.id ) ) return false;

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}
}
