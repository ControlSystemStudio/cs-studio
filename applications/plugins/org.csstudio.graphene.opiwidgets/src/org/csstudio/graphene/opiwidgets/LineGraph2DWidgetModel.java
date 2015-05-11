/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.LineGraph2DWidget;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;
import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;


/**
 * @author shroffk
 *
 */
public class LineGraph2DWidgetModel extends
        AbstractPointDatasetGraph2DWidgetModel {

    public LineGraph2DWidgetModel() {
        super(AbstractSelectionWidgetModelDescription.newModelFrom(LineGraph2DWidget.class));
    }

    public final String ID = "org.csstudio.graphene.opiwidgets.LineGraph2D"; //$NON-NLS-1$

    @Override
    protected void configureProperties() {
        super.configureProperties();
        addProperty(new BooleanProperty(PROP_HIGHLIGHT_SELECTION_VALUE,
                "Highlight Selection Value", WidgetPropertyCategory.Basic, false));
        addProperty(new StringProperty(PROP_SELECTION_VALUE_PV,
                "Selection Value PV (VTable)", WidgetPropertyCategory.Basic, ""));
    }

    @Override
    protected String getDataType() {
        return "VTable/VNumberArray";
    }

    public String getSelectionValuePv() {
        return (String) getCastedPropertyValue(PROP_SELECTION_VALUE_PV);
    }

    public boolean isHighlightSelectionValue() {
        return (Boolean) getCastedPropertyValue(PROP_HIGHLIGHT_SELECTION_VALUE);
    }

    @Override
    public String getTypeID() {
        return ID;
    }

}
