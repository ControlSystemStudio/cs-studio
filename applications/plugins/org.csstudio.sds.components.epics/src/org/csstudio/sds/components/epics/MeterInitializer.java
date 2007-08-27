package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.MeterModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;

/**
 * Initializes the Meter model with EPICS default values.
 * 
 * TODO sh: do some real initializations
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class MeterInitializer extends AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(final AbstractControlSystemSchema schema) {
		initializeDynamicProperty(MeterModel.PROP_VALUE, "$record$");
		// initializeDynamicProperty(MeterModel.PROP_VALUE, "$record$.VAL");
	}
}
