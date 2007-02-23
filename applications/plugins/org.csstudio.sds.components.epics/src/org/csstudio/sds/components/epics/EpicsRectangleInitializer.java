package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.RectangleModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.AliasDescriptor;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.IWidgetModelInitializer;
import org.csstudio.sds.model.logic.DirectConnectionRule;
import org.csstudio.sds.model.logic.ParameterDescriptor;

/**
 * Initializes a rectangle with EPICS specific property values.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 *
 */
public class EpicsRectangleInitializer implements IWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(AbstractWidgetModel model,
			AbstractControlSystemSchema schema) {
		
		assert model instanceof RectangleModel : "Precondition violated: model instanceof RectangleModel"; //$NON-NLS-1$
	
		model.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, schema.getColorProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
		model.setPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND, schema.getColorProperty(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
		
		AliasDescriptor aliasDescriptor = new AliasDescriptor("record", "", "Enter the record name into the 'value' column.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		model.addAliasDescriptor(aliasDescriptor);
		
		final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$record$_calc", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(RectangleModel.PROP_FILL, dynamicsDescriptor);
	}

}
