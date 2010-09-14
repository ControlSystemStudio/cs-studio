//$Id: BigBed.java 15073 2008-08-14 17:32:44Z epbernard $
package org.hibernate.test.annotations.access;

import javax.persistence.Entity;
import javax.persistence.Column;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class BigBed extends Bed {
	@Column(name="bed_size")
	public int size;
}
