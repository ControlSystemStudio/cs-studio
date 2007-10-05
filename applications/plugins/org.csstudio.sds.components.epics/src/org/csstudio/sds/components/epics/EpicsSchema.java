package org.csstudio.sds.components.epics;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.eclipse.swt.graphics.RGB;

/**
 * This initialization schema holds the property values of the EPICS control
 * system.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class EpicsSchema extends AbstractControlSystemSchema {

	/**
	 * Identifier the default foreground color property.
	 */
	public static final String DEFAULT_FOREGROUND_COLOR = "DEFAULT_FOREGROUND_COLOR";

	/**
	 * Identifier the default background color property.
	 */
	public static final String DEFAULT_BACKGROUND_COLOR = "DEFAULT_BACKGROUND_COLOR";

	/**
	 * Identifier the default error color property.
	 */
	public static final String DEFAULT_ERROR_COLOR = "DEFAULT_ERROR_COLOR";

	/**
	 * Identifier the default timelag color property.
	 */
	public static final String DEFAULT_TIMELAG_COLOR = "DEFAULT_TIMELAG_COLOR";
	
	/**
	 * Identifier the record alias property.
	 */
	public static final String RECORD_ALIAS_NAME = "RECORD_ALIAS_NAME";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeWidget(final AbstractWidgetModel widgetModel) {
		widgetModel.setPrimarPv("$channel$");
		// default colors
		widgetModel.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
				getColorProperty(DEFAULT_BACKGROUND_COLOR));
		widgetModel.setPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND,
				getColorProperty(DEFAULT_FOREGROUND_COLOR));
		
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeProperties() {
		addGlobalProperty(DEFAULT_BACKGROUND_COLOR, new RGB(230, 230, 230));
		addGlobalProperty(DEFAULT_FOREGROUND_COLOR, new RGB(0, 0, 192));
		addGlobalProperty(DEFAULT_ERROR_COLOR, new RGB(255, 0, 0));
		addGlobalProperty(DEFAULT_TIMELAG_COLOR, new RGB(255, 0, 255));
		addGlobalProperty(RECORD_ALIAS_NAME, "channel");

		// and so on ..
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeAliases(final AbstractWidgetModel widgetModel) {
		widgetModel.addAlias("channel", "");
	}
}
