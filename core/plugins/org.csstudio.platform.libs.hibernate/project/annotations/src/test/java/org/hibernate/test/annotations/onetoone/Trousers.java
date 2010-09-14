//$Id: Trousers.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.onetoone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Trousers {
	@Id
	public Integer id;

	@OneToOne
	@JoinColumn(name = "zip_id")
	public TrousersZip zip;

}
