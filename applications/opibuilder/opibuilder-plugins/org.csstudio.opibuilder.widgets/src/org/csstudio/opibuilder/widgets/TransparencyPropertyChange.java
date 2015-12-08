package org.csstudio.opibuilder.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.swt.graphics.RGB;

public class TransparencyPropertyChange implements PropertyChangeListener {
	/** transparency color */
	private static final RGB TRANSPARENCY_VALUE = new RGB(240, 240, 240);
	
	private Object oldValue;
	private AbstractWidgetModel model;

	public TransparencyPropertyChange(AbstractWidgetModel model) {
		this.model = model;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (model.getProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND) != null) {
			if ((boolean) evt.getNewValue()) {
				oldValue = model.getProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND)
						.getPropertyValue();
				model.getProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND)
					.setPropertyValue(TRANSPARENCY_VALUE);
			} else {
				model.getProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND)
					.setPropertyValue(oldValue);
			}
		}
	}
}