//$Id: A320.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@DiscriminatorValue("A320")
@Entity()
public class A320 extends Plane {
	private String javaEmbeddedVersion;

	public String getJavaEmbeddedVersion() {
		return javaEmbeddedVersion;
	}

	public void setJavaEmbeddedVersion(String string) {
		javaEmbeddedVersion = string;
	}

}
