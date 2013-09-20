package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.utility.pvmanager.widgets.AlarmProvider;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;

public class AbstractSelectionWidgetModelDescription {

	private boolean enableConfigurableProperty = false;
	private boolean enableBorderAlarmSensitiveProperty = false;
	
	private AbstractSelectionWidgetModelDescription() {
		// Prevent instances
	}

	public boolean isEnableConfigurableProperty() {
		return enableConfigurableProperty;
	}

	public boolean isEnableBorderAlarmSensitiveProperty() {
		return enableBorderAlarmSensitiveProperty;
	}

	public static AbstractSelectionWidgetModelDescription newModel() {
		return new AbstractSelectionWidgetModelDescription();
	}

	public static AbstractSelectionWidgetModelDescription newModelFrom(Class<?> clazz) {
		AbstractSelectionWidgetModelDescription model = newModel();
		if (ConfigurableWidget.class.isAssignableFrom(clazz)) {
			model.enableConfigurableProperty();
		}
		if (AlarmProvider.class.isAssignableFrom(clazz)) {
			model.enableBorderAlarmSensitiveProperty();
		}
		return model;
	}
	
	public AbstractSelectionWidgetModelDescription enableConfigurableProperty() {
		enableConfigurableProperty = true;
		return this;
	}
	
	public AbstractSelectionWidgetModelDescription enableBorderAlarmSensitiveProperty() {
		enableBorderAlarmSensitiveProperty = true;
		return this;
	}
	
}
