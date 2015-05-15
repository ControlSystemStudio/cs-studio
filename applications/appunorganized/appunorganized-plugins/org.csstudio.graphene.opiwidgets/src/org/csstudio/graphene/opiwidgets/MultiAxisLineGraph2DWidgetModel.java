/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import static org.csstudio.graphene.PropertyConstants.PROP_INTERPOLATION_SCHEME;
import static org.csstudio.graphene.PropertyConstants.PROP_SEPARATE_AREAS;

import org.csstudio.graphene.ComboDataUtil;
import org.csstudio.graphene.MultiAxisLineGraph2DWidget;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.MultiAxisLineGraph2DRenderer;


/**
 * @author shroffk
 *
 */
public class MultiAxisLineGraph2DWidgetModel extends
        AbstractPointDatasetGraph2DWidgetModel {

    public MultiAxisLineGraph2DWidgetModel() {
        super(AbstractSelectionWidgetModelDescription.newModelFrom(MultiAxisLineGraph2DWidget.class));
    }

    public final String ID = "org.csstudio.graphene.opiwidgets.MultiAxisLineGraph2D"; //$NON-NLS-1$

    private static String[] suppoertedInterpolations = ComboDataUtil.toStringArray(MultiAxisLineGraph2DRenderer.supportedInterpolationScheme);

    @Override
    protected void configureProperties() {
        super.configureProperties();
        addProperty(new ComboProperty(PROP_INTERPOLATION_SCHEME,
                "Interpolation Scheme", WidgetPropertyCategory.Basic,
                suppoertedInterpolations,
                ComboDataUtil.indexOf(suppoertedInterpolations, MultiAxisLineGraph2DRenderer.DEFAULT_INTERPOLATION_SCHEME.toString())));
        addProperty(new BooleanProperty(PROP_SEPARATE_AREAS,
                "Separate Areas", WidgetPropertyCategory.Basic, false));
    }

    @Override
    protected String getDataType() {
        return "VTable/VNumberArray";
    }

    public InterpolationScheme getInterpolation() {
        return InterpolationScheme.valueOf(suppoertedInterpolations[(Integer) getCastedPropertyValue(PROP_INTERPOLATION_SCHEME)]);
    }

    public boolean isSeparateAreas() {
        return (Boolean) getCastedPropertyValue(PROP_SEPARATE_AREAS);
    }

    @Override
    public String getTypeID() {
        return ID;
    }

}
