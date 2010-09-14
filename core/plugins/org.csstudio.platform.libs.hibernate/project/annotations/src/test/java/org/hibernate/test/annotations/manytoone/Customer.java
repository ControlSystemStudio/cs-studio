//$Id: Customer.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytoone;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Emmanuel Bernard
 */
@Entity(name="DealedCustomer")
public class Customer implements Serializable {
	@Id @GeneratedValue public Integer id;
	public String userId;
}
