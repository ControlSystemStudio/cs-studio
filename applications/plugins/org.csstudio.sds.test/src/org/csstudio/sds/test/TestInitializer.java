package org.csstudio.sds.test;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;

/**
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class TestInitializer extends AbstractWidgetModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(final AbstractWidgetModel model,
			final AbstractControlSystemSchema schema) {
		model.setPropertyValue(TestSchema.PROP_TEST, schema
				.getDoubleProperty(TestSchema.PROP_TEST));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(final AbstractControlSystemSchema schema) {
		initializeStaticProperty(TestSchema.PROP_TEST, schema
				.getDoubleProperty(TestSchema.PROP_TEST));
	}

}
