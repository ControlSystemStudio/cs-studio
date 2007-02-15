package org.csstudio.sds.components.epics;


import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.model.MeterElement;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.IElementModelInitializer;
import org.csstudio.sds.model.logic.ParameterDescriptor;

/**
 * Initializes the Meter element with EPICS default values.
 * 
 * TODO sh: do some real initializations
 * 
 * @author Stefan Hofer
 * @version $Revision$

 */
public class EpicsMeterInitializer implements IElementModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(AbstractElementModel model, AbstractControlSystemSchema schema) {
		if (MeterElement.ID.equals(model.getTypeID())) {
			model.setPropertyValue(AbstractElementModel.PROP_COLOR_FOREGROUND, schema.getColorProperty(AbstractElementModel.PROP_COLOR_FOREGROUND));
			
			final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("scriptedColor"); //$NON-NLS-1$
			dynamicsDescriptor.addInputChannel(new ParameterDescriptor("abc", Double.class)); //$NON-NLS-1$
			model.setDynamicsDescriptor(AbstractElementModel.PROP_COLOR_BACKGROUND, dynamicsDescriptor);
		} else {
			CentralLogger.getInstance().error(this, "The initialization schema of the type '" //$NON-NLS-1$
					+ schema.getTypeId() +"' cannot be applied to a model of type '" //$NON-NLS-1$
					+ model.getTypeID() + "'."); //$NON-NLS-1$
		}
		
	}
}
