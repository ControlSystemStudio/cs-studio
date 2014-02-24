//$Id: ApplicationServer.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.pack.defaultpar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class ApplicationServer {
	private Integer id;
	private String name;
	private org.hibernate.ejb.test.pack.defaultpar.Version version;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(org.hibernate.ejb.test.pack.defaultpar.Version version) {
		this.version = version;
	}
}
