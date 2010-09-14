//$Id: Person.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.onetomany;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
@Table( name = "PERSON_Orderby" )
public class Person implements Serializable {

	private Long idPerson;
	private String firstName, lastName;

	public Person() {
	}

	public void setIdPerson(Long idPerson) {
		this.idPerson = idPerson;
	}

	@Id
	@Column( name = "id_person", nullable = false )
	public Long getIdPerson() {
		return idPerson;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column( name = "first_name", length = 40, nullable = false )
	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column( name = "last_name", length = 40, nullable = false )
	public String getLastName() {
		return lastName;
	}

}

