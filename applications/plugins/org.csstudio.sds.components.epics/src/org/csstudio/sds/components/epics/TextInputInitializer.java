package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;

/**
 * Initializes a rectangle with EPICS specific property values.
 * 
 * @author Stefan Hofer + Sven Wende
 * @version $Revision$
 * 
 */
public final class TextInputInitializer extends
		AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(final AbstractControlSystemSchema schema) {
		initializeStaticProperty(TextInputModel.PROP_INPUT_TEXT, "Enter Text!");
		initializeStaticProperty(TextInputModel.PROP_WIDTH, 100);
		initializeStaticProperty(TextInputModel.PROP_HEIGHT, 50);
		initializeDynamicProperty(TextInputModel.PROP_INPUT_TEXT, "$record$.VAL", "$record$.VAL");
	}

}
