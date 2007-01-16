package org.csstudio.sds.test;

import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.IElementModelInitializer;

/**
 * @author Stefan Hofer
 * @version $Revision$
 *
 */
public class TestInitializer implements IElementModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(AbstractElementModel model,
			AbstractControlSystemSchema schema) {
		model.setPropertyValue(TestSchema.PROP_TEST, schema.getDoubleProperty(TestSchema.PROP_TEST));
	}

}
