//$Id: TennisMatch.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.reflection;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author Emmanuel Bernard
 */
@Entity
@PrimaryKeyJoinColumn(name = "match_id")
@AttributeOverrides(
		{@AttributeOverride(name = "net", column = @Column(name = "net")),
		@AttributeOverride(name = "line", column = @Column(name = "line"))
				})
public class TennisMatch {

}
