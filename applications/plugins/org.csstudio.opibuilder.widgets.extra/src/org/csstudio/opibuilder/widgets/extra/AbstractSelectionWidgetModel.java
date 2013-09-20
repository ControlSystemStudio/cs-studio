/**
 * 
 */
package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractSelectionWidgetModel extends AbstractWidgetModel {

	public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$
	private final boolean enableConfigurableProperty;
	private final boolean enableBorderAlarmSensitiveProperty;
	
	public AbstractSelectionWidgetModel(AbstractSelectionWidgetModelDescription model) {
		this.enableConfigurableProperty = model.isEnableConfigurableProperty();
		this.enableBorderAlarmSensitiveProperty = model.isEnableBorderAlarmSensitiveProperty();
		if (enableConfigurableProperty) {
			addProperty(new BooleanProperty(CONFIGURABLE,
					"Configurable", WidgetPropertyCategory.Behavior, false));
		}
		if (enableBorderAlarmSensitiveProperty) {
			addProperty(new BooleanProperty(IPVWidgetModel.PROP_BORDER_ALARMSENSITIVE, 
					"Alarm Sensitive", WidgetPropertyCategory.Border, true));
		}
	}

	public boolean isEnableConfigurableProperty() {
		return enableConfigurableProperty;
	}
	
	public boolean isConfigurable() {
		if (!enableConfigurableProperty) {
			throw new IllegalStateException("configurable property not enabled");
		}
		return getCastedPropertyValue(CONFIGURABLE);
	}
	
	public boolean isEnableBorderAlarmSensitiveProperty() {
		return enableBorderAlarmSensitiveProperty;
	}

	public boolean isAlarmSensitive() {
		if (!enableBorderAlarmSensitiveProperty) {
			throw new IllegalStateException("border_alarm_sensitive property not enabled");
		}
		return getCastedPropertyValue(IPVWidgetModel.PROP_BORDER_ALARMSENSITIVE);
	}

}
