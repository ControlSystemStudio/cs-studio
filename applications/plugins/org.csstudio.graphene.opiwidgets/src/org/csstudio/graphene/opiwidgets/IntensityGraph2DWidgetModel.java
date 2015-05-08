/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;

import org.csstudio.graphene.IntensityGraph2DWidget;
import org.csstudio.graphene.NumberColorMapUtil;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;
import org.epics.graphene.IntensityGraph2DRenderer;
import org.epics.graphene.NumberColorMap;

/**
 * @author shroffk
 *
 */
public class IntensityGraph2DWidgetModel extends AbstractPointDatasetGraph2DWidgetModel {
    private static int defaultColorMapIndex = NumberColorMapUtil.colorMapIndex(new IntensityGraph2DRenderer().getColorMap());
    private static boolean defaultDrawLegend = new IntensityGraph2DRenderer().isDrawLegend();

    public IntensityGraph2DWidgetModel() {
        super(AbstractSelectionWidgetModelDescription.newModelFrom(IntensityGraph2DWidget.class));
    }

    public final String ID = "org.csstudio.graphene.opiwidgets.IntensityGraph2D"; //$NON-NLS-1$

    @Override
    public String getTypeID() {
        return ID;
    }

    @Override
    protected void configureProperties() {
        super.configureProperties();
        addProperty(new ComboProperty(PROP_COLOR_MAP,
                "Color Map", WidgetPropertyCategory.Basic, NumberColorMapUtil.colorMapNames(), defaultColorMapIndex));
        addProperty(new BooleanProperty(PROP_DRAW_LEGEND,
                "Draw Legend", WidgetPropertyCategory.Basic, defaultDrawLegend));
    }

    public NumberColorMap getColorMap() {
        return NumberColorMapUtil.colorMap((Integer) getCastedPropertyValue(PROP_COLOR_MAP));
    }

    public boolean isDrawLegend() {
        return (Boolean) getCastedPropertyValue(PROP_DRAW_LEGEND);
    }

}
