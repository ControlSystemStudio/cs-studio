/**
 *
 */
package org.csstudio.graphene;

import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidgetAdaptable;
import org.csstudio.utility.pvmanager.widgets.VTypeAdaptable;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.vtype.VType;

/**
 * TODO better ways to handle null parts to the selection
 *
 * @author shroffk
 *
 */
public class MultiAxisLineGraph2DSelection implements VTypeAdaptable,
        ConfigurableWidgetAdaptable {

    private final MultiAxisLineGraph2DWidget widget;

    public MultiAxisLineGraph2DSelection(MultiAxisLineGraph2DWidget widget) {
        this.widget = widget;
    }

    @Override
    public VType toVType() {
        Graph2DResult result = widget.getCurrentResult();
        if (result != null) {
            return result.getData();
        }
        return null;
    }

    @Override
    public ConfigurableWidget toConfigurableWidget() {
        return widget;
    }

}
