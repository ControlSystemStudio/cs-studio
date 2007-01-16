package org.csstudio.sds.components.epics;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.eclipse.swt.graphics.RGB;

/**
 * This initialization schema holds the property values of the EPICS control system.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 *
 */
final public class EpicsSchema extends AbstractControlSystemSchema {

	private static final String ID = "schema.epics"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Object> createPropertyMap() {
		Map<String, Object> properties = new HashMap<String, Object>();
		
		properties.put(AbstractElementModel.PROP_BACKGROUND_COLOR, new RGB(255, 0, 0));
		properties.put(AbstractElementModel.PROP_FOREGROUND_COLOR, new RGB(0, 255, 0));
		//TODO add more properties
		
		return properties;
	}

	@Override
	public String getTypeId() {
		return ID;
	}
}
