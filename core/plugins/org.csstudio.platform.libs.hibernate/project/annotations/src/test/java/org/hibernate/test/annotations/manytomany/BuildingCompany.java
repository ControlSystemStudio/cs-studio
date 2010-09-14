//$Id: BuildingCompany.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytomany;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class BuildingCompany extends Company {
	@Id @GeneratedValue private Long id;
	private Date foundedIn;

	public Date getFoundedIn() {
		return foundedIn;
	}

	public void setFoundedIn(Date foundedIn) {
		this.foundedIn = foundedIn;
	}

	public Long getId() {
		return id;
	}

}
