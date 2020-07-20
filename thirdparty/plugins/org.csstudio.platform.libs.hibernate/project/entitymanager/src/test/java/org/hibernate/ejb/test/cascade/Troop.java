//$Id: Troop.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.cascade;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OrderBy;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Troop implements Serializable {
	private Integer id;
	private String name;
	private Set<Soldier> soldiers;

	@OneToMany(mappedBy = "troop", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	@OrderBy(clause = "name desc")
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	public Set<Soldier> getSoldiers() {
		return soldiers;
	}

	public void setSoldiers(Set<Soldier> soldiers) {
		this.soldiers = soldiers;
	}

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

	public void addSoldier(Soldier s) {
		if ( soldiers == null ) soldiers = new HashSet<Soldier>();
		soldiers.add( s );
		s.setTroop( this );
	}
}
