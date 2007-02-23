package org.csstudio.sds.test;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.IWidgetModelInitializer;

/**
 * @author Stefan Hofer
 * @version $Revision$
 *
 */
public class TestInitializer implements IWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(AbstractWidgetModel model,
			AbstractControlSystemSchema schema) {
		model.setPropertyValue(TestSchema.PROP_TEST, schema.getDoubleProperty(TestSchema.PROP_TEST));
	}

}
