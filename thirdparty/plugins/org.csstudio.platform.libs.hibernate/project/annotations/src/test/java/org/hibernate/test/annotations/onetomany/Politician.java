//$Id: Politician.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.onetomany;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Politician {
	private String name;
	private PoliticalParty party;

	@Id
	@Column(columnDefinition = "VARCHAR(30)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name = "party_fk")
	public PoliticalParty getParty() {
		return party;
	}

	public void setParty(PoliticalParty party) {
		this.party = party;
	}
}
