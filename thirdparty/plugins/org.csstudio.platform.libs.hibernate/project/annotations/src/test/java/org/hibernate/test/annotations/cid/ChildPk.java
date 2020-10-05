//$Id: ChildPk.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.cid;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * Child Pk with many to one inside
 *
 * @author Emmanuel Bernard
 */
@Embeddable
public class ChildPk implements Serializable {
	public int nthChild;
	@ManyToOne()
	@JoinColumns({
	@JoinColumn(name = "parentLastName", referencedColumnName = "p_lname"),
	@JoinColumn(name = "parentFirstName", referencedColumnName = "firstName")
			})
	public Parent parent;

	public boolean equals(Object o) {
		if ( this == o ) return true;
		if ( !( o instanceof ChildPk ) ) return false;

		final ChildPk childPk = (ChildPk) o;

		if ( nthChild != childPk.nthChild ) return false;
		if ( !parent.equals( childPk.parent ) ) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = nthChild;
		result = 29 * result + parent.hashCode();
		return result;
	}
}
