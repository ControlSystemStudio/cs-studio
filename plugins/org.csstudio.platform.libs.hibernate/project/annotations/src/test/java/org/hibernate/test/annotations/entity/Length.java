//$Id: Length.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.entity;

/**
 * @author Emmanuel Bernard
 */
public interface Length<Type> {
	Type getLength();
}
