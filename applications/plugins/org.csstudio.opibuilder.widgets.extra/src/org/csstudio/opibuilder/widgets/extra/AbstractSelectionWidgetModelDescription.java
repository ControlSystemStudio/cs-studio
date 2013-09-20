package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;

public class AbstractSelectionWidgetModelDescription {

	private boolean enableConfigurableProperty = false;
	
	private AbstractSelectionWidgetModelDescription() {
		
	}

	public boolean isEnableConfigurableProperty() {
		return enableConfigurableProperty;
	}

	public static AbstractSelectionWidgetModelDescription newModel() {
		return new AbstractSelectionWidgetModelDescription();
	}

	public static AbstractSelectionWidgetModelDescription newModelFrom(Class<?> clazz) {
		AbstractSelectionWidgetModelDescription model = newModel();
		if (ConfigurableWidget.class.isAssignableFrom(clazz)) {
			model.enableConfigurableProperty();
		}
		return model;
	}
	
	public AbstractSelectionWidgetModelDescription enableConfigurableProperty() {
		enableConfigurableProperty = true;
		return this;
	}
	
}
