//$Id: AddressEntry.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.indexcoll;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class AddressEntry {
	private AddressEntryPk person;
	private String street;
	private String city;
	private AddressBook book;
	private AlphabeticalDirectory directory;

	public boolean equals(Object o) {
		if ( this == o ) return true;
		if ( !( o instanceof AddressEntry ) ) return false;

		final AddressEntry addressEntry = (AddressEntry) o;

		if ( !person.equals( addressEntry.person ) ) return false;

		return true;
	}

	public int hashCode() {
		return person.hashCode();
	}

	@EmbeddedId
	public AddressEntryPk getPerson() {
		return person;
	}

	public void setPerson(AddressEntryPk person) {
		this.person = person;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@ManyToOne
	public AddressBook getBook() {
		return book;
	}

	public void setBook(AddressBook book) {
		this.book = book;
	}

	@ManyToOne
	public AlphabeticalDirectory getDirectory() {
		return directory;
	}

	public void setDirectory(AlphabeticalDirectory directory) {
		this.directory = directory;
	}
}
