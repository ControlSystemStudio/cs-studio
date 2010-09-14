//$Id: Trip.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.override;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

/**
 * @author Emmanuel Bernard
 */
@Entity
@AssociationOverrides({
@AssociationOverride(name = "from", joinColumns = @JoinColumn(name = "from2", nullable = false)),
@AssociationOverride(name = "to", joinColumns = @JoinColumn(name = "to2", nullable = false))
		})
public class Trip extends Move {
}
