//$Id: Organisation.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.onetomany;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Table( name = "ORGANISATION" )
public class Organisation implements Serializable {

	private Long idOrganisation;
	private String name;
	private Set<OrganisationUser> organisationUsers;

	public Organisation() {
	}

	public void setIdOrganisation(Long idOrganisation) {
		this.idOrganisation = idOrganisation;
	}

	@Id
	@Column( name = "id_organisation", nullable = false )
	public Long getIdOrganisation() {
		return idOrganisation;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column( name = "name", nullable = false, length = 40 )
	public String getName() {
		return name;
	}

	public void setOrganisationUsers(Set<OrganisationUser> organisationUsers) {
		this.organisationUsers = organisationUsers;
	}

	@OneToMany( mappedBy = "organisation",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@OrderBy( value = "firstName" )
	public Set<OrganisationUser> getOrganisationUsers() {
		return organisationUsers;
	}

}
