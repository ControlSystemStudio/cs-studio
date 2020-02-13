//$Id: Man.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytomany;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * Man knowing sevezral womens
 *
 * @author Emmanuel Bernard
 */
@Entity
public class Man implements Serializable {
	private ManPk id;
	private String carName;
	private Set<Woman> womens;

	@ManyToMany(cascade = {CascadeType.ALL}, mappedBy = "mens")
	public Set<Woman> getWomens() {
		return womens;
	}

	public void setWomens(Set<Woman> womens) {
		this.womens = womens;
	}

	@Id
	public ManPk getId() {
		return id;
	}

	public void setId(ManPk id) {
		this.id = id;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public int hashCode() {
		//a NPE can occurs, but I don't expect hashcode to be used before pk is set
		return getId().hashCode();
	}

	public boolean equals(Object obj) {
		//a NPE can occurs, but I don't expect equals to be used before pk is set
		if ( obj != null && obj instanceof Man ) {
			return getId().equals( ( (Man) obj ).getId() );
		}
		else {
			return false;
		}
	}

}
