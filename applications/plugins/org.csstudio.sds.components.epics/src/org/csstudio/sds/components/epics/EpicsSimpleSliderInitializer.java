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
public final class EpicsSimpleSliderInitializer extends AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(final AbstractControlSystemSchema schema) {
		initializeDynamicProperty(SimpleSliderModel.PROP_MAX, "$record$.HOPR");
		initializeDynamicProperty(SimpleSliderModel.PROP_MIN, "$record$.LOPR");
		initializeDynamicProperty(SimpleSliderModel.PROP_VALUE, "$record$.VAL", "$record$.VAL");
		
	}
}
