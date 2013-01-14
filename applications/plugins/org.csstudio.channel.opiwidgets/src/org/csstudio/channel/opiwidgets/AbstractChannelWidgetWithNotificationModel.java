package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * This model adds the property for selection notification.
 * 
 * @author carcassi
 */
public abstract class AbstractChannelWidgetWithNotificationModel extends AbstractChannelWidgetModel {
	
	public static final String SELECTION_PV = "selection_pv"; //$NON-NLS-1$	
	public static final String SELECTION_EXPRESSION = "selection_expression"; //$NON-NLS-1$	
	
	@Override
	protected void configureBaseProperties() {
		super.configureBaseProperties();
		addProperty(new StringProperty(SELECTION_PV, "Selection PV", WidgetPropertyCategory.Behavior, ""));
		addProperty(new StringProperty(SELECTION_EXPRESSION, "Selection Expression", WidgetPropertyCategory.Behavior, ""));
	}
	
	public String getSelectionPv() {
		String pvName = getCastedPropertyValue(SELECTION_PV);
		if (pvName.trim().isEmpty())
			return null;
		return pvName;
	}
	
	public String getSelectionExpression() {
		String selectionExpression = getCastedPropertyValue(SELECTION_EXPRESSION);
		if (selectionExpression != null && !selectionExpression.trim().isEmpty()) {
			return selectionExpression;
		}
		
		return defaultSelectionExpression();
	}
	
	protected String defaultSelectionExpression() {
		return "#(Channel Name)";
	}

}
