package org.csstudio.sds.test;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;

/**
* A initialization schema for unit tests. 
* 
* @author Stefan Hofer
* @version $Revision$
*
*/
public final class TestSchema extends AbstractControlSystemSchema {
	
	/**
	 * ID for a test property.
	 */
	public static final String PROP_TEST = "PROP_TEST"; //$NON-NLS-1$

	@Override
	protected Map<String, Object> createPropertyMap() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PROP_TEST, 3.14);
		return properties;
	}

	@Override
	public String getTypeId() {
		return "schema.test"; //$NON-NLS-1$
	}
}
