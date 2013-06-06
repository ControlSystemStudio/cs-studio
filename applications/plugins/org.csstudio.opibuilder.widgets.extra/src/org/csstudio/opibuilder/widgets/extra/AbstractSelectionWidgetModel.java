/**
 * 
 */
package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractSelectionWidgetModel extends AbstractWidgetModel {

	public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$
	private final Class<? extends Composite> widgetClass;
	
	public AbstractSelectionWidgetModel(Class<? extends Composite> widgetClass) {
		this.widgetClass = widgetClass;
		if (ConfigurableWidget.class.isAssignableFrom(this.widgetClass)) {
			addProperty(new BooleanProperty(CONFIGURABLE,
					"Configurable", WidgetPropertyCategory.Basic, false));
		}
	}

	public boolean isConfigurable() {
		return getCastedPropertyValue(CONFIGURABLE);
	}

}
