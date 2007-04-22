package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.RectangleModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;

/**
 * Initializes a rectangle with EPICS specific property values.
 * 
 * @author Stefan Hofer + Sven Wende
 * @version $Revision$
 * 
 */
public final class EpicsRectangleInitializer extends
		AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(final AbstractControlSystemSchema schema) {
		initializeStaticProperty(RectangleModel.PROP_FILL, 50.0);
		initializeDynamicProperty(RectangleModel.PROP_FILL, "$record$.VAL");
	}

}
