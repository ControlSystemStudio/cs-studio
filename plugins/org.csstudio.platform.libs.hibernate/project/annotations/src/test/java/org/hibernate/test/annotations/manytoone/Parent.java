//$Id: Parent.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytoone;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Table(name = "tbl_parent")
public class Parent implements Serializable {
	@Id
	public ParentPk id;
	public int age;

	public int hashCode() {
		//a NPE can occurs, but I don't expect hashcode to be used before pk is set
		return id.hashCode();
	}

	public boolean equals(Object obj) {
		//a NPE can occurs, but I don't expect equals to be used before pk is set
		if ( obj != null && obj instanceof Parent ) {
			return id.equals( ( (Parent) obj ).id );
		}
		else {
			return false;
		}
	}
}
