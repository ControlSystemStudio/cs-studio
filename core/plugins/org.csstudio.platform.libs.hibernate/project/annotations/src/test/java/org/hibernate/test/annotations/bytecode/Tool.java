//$Id: Tool.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.bytecode;

/**
 * @author Emmanuel Bernard
 */
public interface Tool {
	public Long getId();

	public void setId(Long id);

	public Number usage();
}
