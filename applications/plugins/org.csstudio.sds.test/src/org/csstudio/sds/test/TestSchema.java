package org.csstudio.sds.test;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;

/**
 * A initialization schema for unit tests.
 * 
 * @author Stefan Hofer, Sven Wende
 * @version $Revision$
 */
public final class TestSchema extends AbstractControlSystemSchema {

	/**
	 * ID for a test property.
	 */
	public static final String PROP_TEST = "PROP_TEST"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeAliases(final AbstractWidgetModel widgetModel) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeProperties() {
		addGlobalProperty(PROP_TEST, 3.14);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeWidget(final AbstractWidgetModel widgetModel) {

	}
}
