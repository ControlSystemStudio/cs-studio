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

		AliasDescriptor aliasDescriptor2 = new AliasDescriptor("record2", "", "Enter the record name into the 'value' column.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		model.addAliasDescriptor(aliasDescriptor2);
		
		AliasDescriptor aliasDescriptor3 = new AliasDescriptor("record3", "", "Enter the record name into the 'value' column.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		model.addAliasDescriptor(aliasDescriptor3);
		
		AliasDescriptor aliasDescriptor4 = new AliasDescriptor("record4", "", "Enter the record name into the 'value' column.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		model.addAliasDescriptor(aliasDescriptor4);
		
		AliasDescriptor aliasDescriptor5 = new AliasDescriptor("record5", "", "Enter the record name into the 'value' column.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		model.addAliasDescriptor(aliasDescriptor5);
		
		final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$record$:VAL", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_FILL, dynamicsDescriptor);
		
		final DynamicsDescriptor dynamicMinDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicMinDescriptor.addInputChannel(new ParameterDescriptor("$record$:MIN", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_MIN, dynamicMinDescriptor);
		
		final DynamicsDescriptor dynamicMaxDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicMaxDescriptor.addInputChannel(new ParameterDescriptor("$record$:MAX", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(BargraphModel.PROP_MAX, dynamicMaxDescriptor);
	}

}
