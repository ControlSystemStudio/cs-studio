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
public final class BargraphInitializer extends
		AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(final AbstractControlSystemSchema schema) {
		initializeDynamicProperty(BargraphModel.PROP_MIN, "$record$.LOPR");
		initializeDynamicProperty(BargraphModel.PROP_MAX, "$record$.HOPR");
		initializeDynamicProperty(BargraphModel.PROP_HIHI_LEVEL,
				"$record$.HIHI");
		initializeDynamicProperty(BargraphModel.PROP_HI_LEVEL, "$record$.HIGH");
		initializeDynamicProperty(BargraphModel.PROP_LOLO_LEVEL,
				"$record$.LOLO");
		initializeDynamicProperty(BargraphModel.PROP_LO_LEVEL, "$record$.LOW");
		initializeDynamicProperty(BargraphModel.PROP_FILL, "$record$.VAL");
	}
}
