/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.opiwidgets.properties.AxisRangeProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModel;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;
import org.epics.graphene.AxisRange;
import org.epics.graphene.AxisRanges;

import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;

/**
 * @author shroffk
 *
 */
public abstract class AbstractGraph2DWidgetModel extends AbstractSelectionWidgetModel {

    public AbstractGraph2DWidgetModel(AbstractSelectionWidgetModelDescription model) {
        super(model);
    }


    @Override
    protected void configureProperties() {
        addProperty(new StringProperty(PROP_DATA_FORMULA,
                "Data Expression (" + getDataType() + ")", WidgetPropertyCategory.Basic, ""));
        addProperty(new BooleanProperty(PROP_RESIZABLE_AXIS,
                "Resizable Axis", WidgetPropertyCategory.Behavior, true));
        addProperty(new AxisRangeProperty(PROP_X_AXIS_RANGE,
                "X axis range", WidgetPropertyCategory.Basic, AxisRanges.display()));
        addProperty(new AxisRangeProperty(PROP_Y_AXIS_RANGE,
                "Y axis range", WidgetPropertyCategory.Basic, AxisRanges.display()));
    }

    protected abstract String getDataType();

    public String getDataFormula() {
        return (String) getCastedPropertyValue(PROP_DATA_FORMULA);
    }


    public boolean isResizableAxis() {
        return getCastedPropertyValue(PROP_RESIZABLE_AXIS);
    }

    public AxisRange getXAxisRange() {
        return (AxisRange) getCastedPropertyValue(PROP_X_AXIS_RANGE);
    }

    public AxisRange getYAxisRange() {
        return (AxisRange) getCastedPropertyValue(PROP_Y_AXIS_RANGE);
    }

}
