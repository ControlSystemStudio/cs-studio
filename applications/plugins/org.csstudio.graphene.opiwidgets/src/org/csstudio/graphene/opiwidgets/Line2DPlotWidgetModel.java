/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * @author shroffk
 * 
 */
public class Line2DPlotWidgetModel extends AbstractWidgetModel {

    public final String ID = "org.csstudio.graphene.opiwidgets.Line2DPlot"; //$NON-NLS-1$

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
    }

    public ProcessVariable getProcessVariable() {
	return new ProcessVariable(
		(String) getCastedPropertyValue(AbstractPVWidgetModel.PROP_PVNAME));
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
