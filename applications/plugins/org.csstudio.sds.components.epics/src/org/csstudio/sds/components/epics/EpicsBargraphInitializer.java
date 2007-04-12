package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.AliasDescriptor;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.IWidgetModelInitializer;
import org.csstudio.sds.model.logic.DirectConnectionRule;
import org.csstudio.sds.model.logic.ParameterDescriptor;

/**
 * Initializes a bargraph with EPICS specific property values.
 * 
 * @author Kai Meyer
 *
 */
public final class EpicsBargraphInitializer implements IWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(final AbstractWidgetModel model, final AbstractControlSystemSchema schema) {
		
		assert model instanceof BargraphModel : "Precondition violated: model instanceof BargraphModel"; //$NON-NLS-1$
	
		model.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, schema.getColorProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
		model.setPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND, schema.getColorProperty(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
		
		AliasDescriptor aliasDescriptor = new AliasDescriptor("record", "", "Enter the record name into the 'value' column.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		model.addAliasDescriptor(aliasDescriptor);
		
		final DynamicsDescriptor dynamicsFillDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicsFillDescriptor.addInputChannel(new ParameterDescriptor("$record$:VAL", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_FILL, dynamicsFillDescriptor);
		
		final DynamicsDescriptor dynamicMinDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicMinDescriptor.addInputChannel(new ParameterDescriptor("$record$:MIN", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_MIN, dynamicMinDescriptor);
		
		final DynamicsDescriptor dynamicMaxDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicMaxDescriptor.addInputChannel(new ParameterDescriptor("$record$:MAX", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_MAX, dynamicMaxDescriptor);
		
		final DynamicsDescriptor dynamicLoloDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicLoloDescriptor.addInputChannel(new ParameterDescriptor("$record$:LOLO", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_LOLO_LEVEL, dynamicLoloDescriptor);
		
		final DynamicsDescriptor dynamicLoDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicLoDescriptor.addInputChannel(new ParameterDescriptor("$record$:LO", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_LO_LEVEL, dynamicLoDescriptor);
		
		final DynamicsDescriptor dynamicMDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicMDescriptor.addInputChannel(new ParameterDescriptor("$record$:M", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_M_LEVEL, dynamicMDescriptor);
		
		final DynamicsDescriptor dynamicHiDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicHiDescriptor.addInputChannel(new ParameterDescriptor("$record$:HI", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_HI_LEVEL, dynamicHiDescriptor);
		
		final DynamicsDescriptor dynamicHihiDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicHihiDescriptor.addInputChannel(new ParameterDescriptor("$record$:HIHI", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_HIHI_LEVEL, dynamicHihiDescriptor);
	}

}
