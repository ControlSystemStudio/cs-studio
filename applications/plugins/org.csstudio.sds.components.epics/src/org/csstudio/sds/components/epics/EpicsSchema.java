package org.csstudio.sds.components.epics;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.eclipse.swt.graphics.RGB;

/**
 * This initialization schema holds the property values of the EPICS control
 * system.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class EpicsSchema extends AbstractControlSystemSchema {

	/**
	 * The schema ID.
	 */
	private static final String ID = "schema.epics"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Object> createPropertyMap() {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put(AbstractWidgetModel.PROP_COLOR_BACKGROUND, new RGB(255,
				0, 0));
		properties.put(AbstractWidgetModel.PROP_COLOR_FOREGROUND, new RGB(0,
				255, 0));
		// TODO add more properties

		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeId() {
		return ID;
	}
}
