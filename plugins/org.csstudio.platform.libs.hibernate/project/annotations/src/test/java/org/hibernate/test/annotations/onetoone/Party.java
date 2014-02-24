//$Id: Party.java 19881 2010-07-01 11:09:45Z sharathjreddy $
package org.hibernate.test.annotations.onetoone;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Party {
	@Id
	String partyId;

	@OneToOne(cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	PartyAffiliate partyAffiliate;
}
