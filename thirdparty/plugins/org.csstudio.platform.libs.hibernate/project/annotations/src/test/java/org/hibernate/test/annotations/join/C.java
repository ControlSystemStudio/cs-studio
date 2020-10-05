//$Id: C.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.join;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.SecondaryTable;

/**
 * @author Emmanuel Bernard
 */
@Entity
@DiscriminatorValue("C")
@SecondaryTable(name="C")
public class C extends B {
	@Column(table = "C") private int age;


	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
