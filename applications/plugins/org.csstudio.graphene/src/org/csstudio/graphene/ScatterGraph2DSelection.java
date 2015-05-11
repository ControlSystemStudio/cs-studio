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
 * @author shroffk
 *
 */
public class ScatterGraph2DSelection implements VTypeAdaptable,
        ConfigurableWidgetAdaptable {

    private final ScatterGraph2DWidget widget;

    public ScatterGraph2DSelection(ScatterGraph2DWidget widget) {
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
