/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;


/**
 * @author shroffk
 * 
 */
public class LineGraph2DWidgetModel extends
		AbstractPointDatasetGraph2DWidgetModel {

	public final String ID = "org.csstudio.graphene.opiwidgets.LineGraph2D"; //$NON-NLS-1$
	
	public static final String PROP_HIGHLIGHT_FOCUS_VALUE = "highlight_focus_value"; //$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new BooleanProperty(PROP_HIGHLIGHT_FOCUS_VALUE,
				"Highlight Focus Value", WidgetPropertyCategory.Basic, false));
	}

	public boolean isHighlightFocusValue() {
		return (Boolean) getCastedPropertyValue(PROP_HIGHLIGHT_FOCUS_VALUE);
	}

	@Override
	public String getTypeID() {
		return ID;
	}

}
