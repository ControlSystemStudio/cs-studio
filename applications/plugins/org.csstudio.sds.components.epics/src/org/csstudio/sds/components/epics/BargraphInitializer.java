package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;

/**
 * Initializes a bargraph with EPICS specific property values.
 * 
 * @author Kai Meyer
 * 
 */
public final class BargraphInitializer extends AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(final AbstractControlSystemSchema schema) {
		initializeDynamicProperty(BargraphModel.PROP_MIN, "$channel$.LOPR");
		initializeDynamicProperty(BargraphModel.PROP_MAX, "$channel$.HOPR");
		initializeDynamicProperty(BargraphModel.PROP_HIHI_LEVEL,
				"$channel$.HIHI");
		initializeDynamicProperty(BargraphModel.PROP_HI_LEVEL, "$channel$.HIGH");
		initializeDynamicProperty(BargraphModel.PROP_LOLO_LEVEL,
				"$channel$.LOLO");
		initializeDynamicProperty(BargraphModel.PROP_LO_LEVEL, "$channel$.LOW");
		initializeDynamicProperty(BargraphModel.PROP_FILL, "$channel$");
		// initializeDynamicProperty(BargraphModel.PROP_FILL, "$channel$.VAL");
	}
}
