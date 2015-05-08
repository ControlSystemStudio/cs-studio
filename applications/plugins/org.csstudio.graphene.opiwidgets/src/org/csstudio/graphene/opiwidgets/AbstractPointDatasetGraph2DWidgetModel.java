/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;

import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;

/**
 * @author shroffk
 *
 */
public abstract class AbstractPointDatasetGraph2DWidgetModel extends AbstractGraph2DWidgetModel {

    public AbstractPointDatasetGraph2DWidgetModel(AbstractSelectionWidgetModelDescription model) {
        super(model);
    }


    @Override
    protected void configureProperties() {
        super.configureProperties();
        addProperty(new StringProperty(PROP_X_FORMULA,
                "X Column Expression (VString)", WidgetPropertyCategory.Basic, ""));
        addProperty(new StringProperty(PROP_Y_FORMULA,
                "Y Column Expression (VString)", WidgetPropertyCategory.Basic, ""));

    }

    protected String getDataType() {
        return "VTable";
    }

    public String getXColumnFormula() {
        return (String) getCastedPropertyValue(PROP_X_FORMULA);
    }

    public String getYColumnFormula() {
        return (String) getCastedPropertyValue(PROP_Y_FORMULA);
    }

}
