package org.csstudio.sds.components.epics;

import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.IElementModelInitializer;

/**
 * @author Stefan Hofer
 * @version $Revision$
 *
 */
public class EpicsRectangleInitializer implements IElementModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(AbstractElementModel model,
			AbstractControlSystemSchema schema) {
		model.setPropertyValue(AbstractElementModel.PROP_BACKGROUND_COLOR, schema.getColorProperty(AbstractElementModel.PROP_BACKGROUND_COLOR));
		model.setPropertyValue(AbstractElementModel.PROP_FOREGROUND_COLOR, schema.getColorProperty(AbstractElementModel.PROP_FOREGROUND_COLOR));
	}

}
