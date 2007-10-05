package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.SimpleSliderModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;

/**
 * Widget initializer for a simple slider for EPICs.
 * 
 * @author Sven Wende
 * 
 */
public final class SimpleSliderInitializer extends
		AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(final AbstractControlSystemSchema schema) {
		initializeDynamicProperty(SimpleSliderModel.PROP_MAX, "$channel$.HOPR");
		initializeDynamicProperty(SimpleSliderModel.PROP_MIN, "$channel$.LOPR");
		initializeDynamicProperty(SimpleSliderModel.PROP_VALUE, "$channel$",
				"$channel$");
		// initializeDynamicProperty(SimpleSliderModel.PROP_VALUE,
		// "$channel$.VAL", "$channel$.VAL");
	}
}
