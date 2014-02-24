//$Id: B.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.xml.hbm;

/**
 * @author Emmanuel Bernard
 */
public interface B extends A {
	public Integer getBId();

	public void setBId(Integer bId);
}
