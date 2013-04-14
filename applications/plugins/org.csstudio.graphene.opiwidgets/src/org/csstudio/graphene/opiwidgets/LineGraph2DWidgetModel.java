/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * @author shroffk
 * 
 */
public class LineGraph2DWidgetModel extends AbstractWidgetModel {

    public final String ID = "org.csstudio.graphene.opiwidgets.LineGraph2D"; //$NON-NLS-1$

    public static final String PROP_XPVNAME = "x_pv_name"; //$NON-NLS-1$
    public static final String PROP_SHOW_AXIS = "show_axis"; //$NON-NLS-1$
    public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.csstudio.opibuilder.model.AbstractWidgetModel#configureProperties()
     */
    @Override
    protected void configureProperties() {
	addProperty(new StringProperty(AbstractPVWidgetModel.PROP_PVNAME,
		"PV Name", WidgetPropertyCategory.Basic, ""));
	addProperty(new StringProperty(LineGraph2DWidgetModel.PROP_XPVNAME,
		"X PV Name", WidgetPropertyCategory.Basic, ""));
	addProperty(new BooleanProperty(LineGraph2DWidgetModel.CONFIGURABLE,
		"configurable", WidgetPropertyCategory.Basic, true));
	addProperty(new BooleanProperty(LineGraph2DWidgetModel.PROP_SHOW_AXIS,
		"Show Axis", WidgetPropertyCategory.Display, true));

    }

    public ProcessVariable getProcessVariable() {
	return new ProcessVariable(
		(String) getCastedPropertyValue(AbstractPVWidgetModel.PROP_PVNAME));
    }

    public String getXPvName() {
	return (String) getCastedPropertyValue(LineGraph2DWidgetModel.PROP_XPVNAME);
    }

    public boolean getShowAxis() {
	return getCastedPropertyValue(LineGraph2DWidgetModel.PROP_SHOW_AXIS);
    }

    public boolean isConfigurable() {
	return getCastedPropertyValue(CONFIGURABLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.csstudio.opibuilder.model.AbstractWidgetModel#getTypeID()
     */
    @Override
    public String getTypeID() {
	return ID;
    }

}
