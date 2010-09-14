//$Id: Tooth.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Tooth {
	@Id
	@GeneratedValue
	public Integer id;
	public String type;
	@ManyToOne(cascade = CascadeType.PERSIST)
	public Tooth leftNeighbour;
	@ManyToOne(cascade = CascadeType.MERGE)
	public Tooth rightNeighbour;
	@ManyToOne
	public Mouth mouth;
}
