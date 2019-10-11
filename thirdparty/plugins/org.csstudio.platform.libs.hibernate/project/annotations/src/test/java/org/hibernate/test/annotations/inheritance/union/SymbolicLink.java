//$Id: SymbolicLink.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.inheritance.union;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SymbolicLinkUnion")
public class SymbolicLink extends File {

	File target;

	SymbolicLink() {
	}

	public SymbolicLink(File target) {
		this.target = target;
	}

	@ManyToOne(optional = false)
	public File getTarget() {
		return target;
	}

	public void setTarget(File target) {
		this.target = target;
	}


}
