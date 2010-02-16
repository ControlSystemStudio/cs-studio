package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class XYGraphModel extends AbstractWidgetModel {

	public static final String PROP_SHOW_TOOLBAR = "PROP_SHOW_TOOLBAR";
	public static final String PROP_TRANSPARENT = "PROP_TRANSPARENT";
	public static final String PROP_SHOW_TITLE = "PROP_SHOW_TITLE";
	public static final String PROP_TITLE = "PROP_TITLE";
	public static final String PROP_SHOW_LEGEND = "PROP_SHOW_LEGEND";
	public static final String PROP_TITLE_COLOR = "PROP_TITLE_COLOR";
	public static final String PROP_TITLE_FONT = "PROP_TITLE_FONT";
	public static final String ID = "org.csstudio.sds.components.XYGraph";

	@Override
	protected void configureProperties() {
		addProperty(PROP_SHOW_TOOLBAR, new BooleanProperty("Show Toolbar", WidgetPropertyCategory.Display, false));
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent", WidgetPropertyCategory.Display, true));
		addProperty(PROP_SHOW_TITLE, new BooleanProperty("Show Title", WidgetPropertyCategory.Display, false));
		addProperty(PROP_TITLE, new StringProperty("Title", WidgetPropertyCategory.Display, ""));
		addProperty(PROP_TITLE_COLOR, new ColorProperty("Title Color", WidgetPropertyCategory.Display, new RGB(0,0,0)));
		addProperty(PROP_TITLE_FONT, new FontProperty("Title Font", WidgetPropertyCategory.Display, new FontData("Arial", 8, SWT.NONE)));
		addProperty(PROP_SHOW_LEGEND, new BooleanProperty("Show Legend", WidgetPropertyCategory.Display, false));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public boolean isToolbarVisible() {
		return getProperty(PROP_SHOW_TOOLBAR).getPropertyValue();
	}
	
	public boolean isLegendVisible() {
		return getProperty(PROP_SHOW_LEGEND).getPropertyValue();
	}
	
	public boolean isTransparent() {
		return getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	public boolean isTitleVisible() {
		return getProperty(PROP_SHOW_TITLE).getPropertyValue();
	}
	
	public String getTitle() {
		return getProperty(PROP_TITLE).getPropertyValue();
	}
	
	public ColorProperty getTitleColor() {
		return (ColorProperty) getProperty(PROP_TITLE_COLOR);
	}
	
	public FontData getTitleFont() {
		return getProperty(PROP_TITLE_FONT).getPropertyValue();
	}
	
}
