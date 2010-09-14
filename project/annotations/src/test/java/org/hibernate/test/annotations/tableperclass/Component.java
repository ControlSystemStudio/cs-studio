//$Id: Component.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.tableperclass;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.Index;

/**
 * @author Emmanuel Bernard
 */
@Entity(name = "xpmComponent")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Component {
	private String manufacturerPartNumber;
	private Long manufacturerId;
	private Long id;



	public void setId(Long id) {
		this.id = id;
	}


	@Id
	public Long getId() {
		return id;
	}

	@Column(nullable = false)
	@Index(name = "manufacturerPartNumber")
	public String getManufacturerPartNumber() {
		return manufacturerPartNumber;
	}

	@Column(nullable = false)
	public Long getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}


	public void setManufacturerPartNumber(String manufacturerPartNumber) {
		this.manufacturerPartNumber = manufacturerPartNumber;
	}
}