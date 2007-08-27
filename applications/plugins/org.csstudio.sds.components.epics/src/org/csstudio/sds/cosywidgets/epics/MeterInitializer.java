package org.csstudio.sds.cosywidgets.epics;

import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;

import org.csstudio.sds.components.model.MeterModel;

/**
 * Initializes a meter with EPICS specific property values.
 * 
 * @author jbercic
 * 
 */
public final class MeterInitializer extends AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(final AbstractControlSystemSchema schema) {
		initializeDynamicProperty(MeterModel.PROP_MINVAL, "$record$.LOPR");
		initializeDynamicProperty(MeterModel.PROP_MAXVAL, "$record$.HOPR");
		initializeDynamicProperty(MeterModel.PROP_HIHIBOUND, "$record$.HIHI");
		initializeDynamicProperty(MeterModel.PROP_HIBOUND, "$record$.HIGH");
		initializeDynamicProperty(MeterModel.PROP_LOLOBOUND, "$record$.LOLO");
		initializeDynamicProperty(MeterModel.PROP_LOBOUND, "$record$.LOW");
		initializeDynamicProperty(MeterModel.PROP_VALUE, "$record$");
		// initializeDynamicProperty(MeterModel.PROP_VALUE,"$record$.VAL");
		this.initializeStaticProperty(MeterModel.PROP_VALUE, (Double) 0.25);
		this.initializeStaticProperty(MeterModel.PROP_MBOUND, (Double) 0.5);
	}
}
