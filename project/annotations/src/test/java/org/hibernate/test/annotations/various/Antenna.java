//$Id: Antenna.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.various;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Antenna {
	@Id public Integer id;
	@Generated(GenerationTime.ALWAYS) @Column()
	public String longitude;

	@Generated(GenerationTime.INSERT) @Column(insertable = false)
	public String latitude;

	@Generated(GenerationTime.NEVER)
	public Double power;
}
