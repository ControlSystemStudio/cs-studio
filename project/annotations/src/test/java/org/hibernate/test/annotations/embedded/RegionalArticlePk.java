//$Id: RegionalArticlePk.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.embedded;

import java.io.Serializable;
import javax.persistence.Embeddable;

import org.hibernate.annotations.AccessType;

/**
 * Regional article pk
 *
 * @author Emmanuel Bernard
 */
@Embeddable
@AccessType("field")
public class RegionalArticlePk implements Serializable {
	/**
	 * country iso2 code
	 */
	public String iso2;
	public String localUniqueKey;

	public int hashCode() {
		//this implem sucks
		return ( iso2 + localUniqueKey ).hashCode();
	}

	public boolean equals(Object obj) {
		//iso2 and localUniqueKey are expected to be set in this implem
		if ( obj != null && obj instanceof RegionalArticlePk ) {
			RegionalArticlePk other = (RegionalArticlePk) obj;
			return iso2.equals( other.iso2 ) && localUniqueKey.equals( other.localUniqueKey );
		}
		else {
			return false;
		}
	}
}
