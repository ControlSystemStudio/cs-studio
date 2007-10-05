package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.EllipseModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;

/**
 * Initializes a rectangle with EPICS specific property values.
 * 
 * @author Stefan Hofer + Sven Wende
 * @version $Revision$
 * 
 */
public final class EllipseInitializer extends AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(final AbstractControlSystemSchema schema) {
		initializeStaticProperty(EllipseModel.PROP_FILL, 50.0);
		initializeDynamicProperty(EllipseModel.PROP_FILL, "$channel$");
		// initializeDynamicProperty(EllipseModel.PROP_FILL, "$channel$.VAL");
	}

}
