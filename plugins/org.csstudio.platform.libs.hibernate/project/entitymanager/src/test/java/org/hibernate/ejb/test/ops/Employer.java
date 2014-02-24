//$Id: Employer.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.ops;

import java.io.Serializable;
import java.util.Collection;


/**
 * Employer in a employer-Employee relationship
 *
 * @author Emmanuel Bernard
 */

public class Employer implements Serializable {
	private Integer id;
	private Collection employees;


	public Collection getEmployees() {
		return employees;
	}


	public Integer getId() {
		return id;
	}

	public void setEmployees(Collection set) {
		employees = set;
	}

	public void setId(Integer integer) {
		id = integer;
	}
}
