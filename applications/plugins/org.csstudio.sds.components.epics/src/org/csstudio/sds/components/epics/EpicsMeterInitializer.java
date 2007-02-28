package org.csstudio.sds.components.epics;


import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.model.MeterModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.AliasDescriptor;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.IWidgetModelInitializer;
import org.csstudio.sds.model.logic.ParameterDescriptor;

/**
 * Initializes the Meter model with EPICS default values.
 * 
 * TODO sh: do some real initializations
 * 
 * @author Stefan Hofer
 * @version $Revision$

 */
public class EpicsMeterInitializer implements IWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(AbstractWidgetModel model, AbstractControlSystemSchema schema) {
		if (MeterModel.ID.equals(model.getTypeID())) {
			model.setPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND, schema.getColorProperty(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
			
			AliasDescriptor aliasDescriptor = new AliasDescriptor("input", "", "Enter the record name into the 'value' column.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
			model.addAliasDescriptor(aliasDescriptor);

			
			final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("scriptedColor"); //$NON-NLS-1$
			dynamicsDescriptor.addInputChannel(new ParameterDescriptor("abc", Double.class)); //$NON-NLS-1$
			model.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, dynamicsDescriptor);
		} else {
			CentralLogger.getInstance().error(this, "The initialization schema of the type '" //$NON-NLS-1$
					+ schema.getTypeId() +"' cannot be applied to a model of type '" //$NON-NLS-1$
					+ model.getTypeID() + "'."); //$NON-NLS-1$
		}
		
	}
}
