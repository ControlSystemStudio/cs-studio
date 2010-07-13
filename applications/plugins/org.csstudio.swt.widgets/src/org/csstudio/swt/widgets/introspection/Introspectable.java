package org.csstudio.swt.widgets.introspection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

/**An introspectable object can give its bean information. All CSS widgets in this library
 * must implement this interface. 
 * @author Xihui Chen
 *
 */
public interface Introspectable {

	public BeanInfo getBeanInfo() throws IntrospectionException;
	
}
