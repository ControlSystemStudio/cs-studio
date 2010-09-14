//$Id: Deal.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytoone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Deal {
	@Id @GeneratedValue public Integer id;
	@ManyToOne @JoinColumn(referencedColumnName = "userId") public Customer from;
	@ManyToOne @JoinColumn(referencedColumnName = "userId") public Customer to;

}
