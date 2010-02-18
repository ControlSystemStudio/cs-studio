package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

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
		addColorProperty(PROP_TITLE_COLOR, "Title Color", WidgetPropertyCategory.Display, "#000000");
		addFontProperty(PROP_TITLE_FONT, "Title Font", WidgetPropertyCategory.Display, new FontData("Arial", 8, SWT.NONE));
		addProperty(PROP_SHOW_LEGEND, new BooleanProperty("Show Legend", WidgetPropertyCategory.Display, false));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public boolean isToolbarVisible() {
		return getBooleanProperty(PROP_SHOW_TOOLBAR).getPropertyValue();
	}
	
	public boolean isLegendVisible() {
		return getBooleanProperty(PROP_SHOW_LEGEND).getPropertyValue();
	}
	
	public boolean isTransparent() {
		return getBooleanProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	public boolean isTitleVisible() {
		return getBooleanProperty(PROP_SHOW_TITLE).getPropertyValue();
	}
	
	public String getTitle() {
		return getStringProperty(PROP_TITLE).getPropertyValue();
	}

}
