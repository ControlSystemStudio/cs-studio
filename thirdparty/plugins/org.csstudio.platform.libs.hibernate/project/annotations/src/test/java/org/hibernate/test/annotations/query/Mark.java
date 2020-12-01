//$Id: Mark.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.query;

import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

/**
 * Example of scalar result (not working right now)
 *
 * @author Emmanuel Bernard
 */
@Entity
@NamedNativeQuery(name = "average", query = "select avg(m.value) from Mark m", resultSetMapping = "columnmapping")
@SqlResultSetMapping(
		name = "columnmapping",
		columns = @ColumnResult(name = "aver")
)
public class Mark {
	@Id
	@GeneratedValue
	public int id;
	public int value;
}
