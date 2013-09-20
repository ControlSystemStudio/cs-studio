/**
 * 
 */
package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractSelectionWidgetModel extends AbstractWidgetModel {

	public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$
	private final boolean enableConfigurableProperty;
	
	public AbstractSelectionWidgetModel(AbstractSelectionWidgetModelDescription model) {
		this.enableConfigurableProperty = model.isEnableConfigurableProperty();
		if (enableConfigurableProperty) {
			addProperty(new BooleanProperty(CONFIGURABLE,
					"Configurable", WidgetPropertyCategory.Behavior, false));
		}
	}

	public boolean isConfigurable() {
		if (!enableConfigurableProperty) {
			throw new IllegalStateException("configurable property not enabled");
		}
		return getCastedPropertyValue(CONFIGURABLE);
	}

}
