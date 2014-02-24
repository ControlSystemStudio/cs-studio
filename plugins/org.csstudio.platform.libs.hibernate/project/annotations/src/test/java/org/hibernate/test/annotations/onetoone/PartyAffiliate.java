//$Id: PartyAffiliate.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.onetoone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class PartyAffiliate {
	@Id
	String partyId;

	@OneToOne(mappedBy="partyAffiliate")
	Party party;

	String affiliateName;
}
