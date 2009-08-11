package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;

public class EllipseModel extends AbstractWidgetModel {
	
	
	public final String ID = "org.csstudio.opibuilder.widgets.Ellipse";

	
	
	@Override
	protected void configureProperties() {
		
		setSize(100, 50);
		setBackgroundColor(CustomMediaFactory.COLOR_BLUE);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
		
	}

	@Override
	public String getTypeID() {
		return ID;
	}

}
