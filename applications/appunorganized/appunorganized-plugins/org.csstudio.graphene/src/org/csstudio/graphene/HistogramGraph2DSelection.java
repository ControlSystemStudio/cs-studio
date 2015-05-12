/**
 *
 */
package org.csstudio.graphene;

import java.util.Collection;
import java.util.Collections;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidgetAdaptable;
import org.csstudio.utility.pvmanager.widgets.ProcessVariableAdaptable;
import org.csstudio.utility.pvmanager.widgets.VTypeAdaptable;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.vtype.VType;

/**
 * @author shroffk
 *
 */
public class HistogramGraph2DSelection implements VTypeAdaptable,
        ProcessVariableAdaptable,
        ConfigurableWidgetAdaptable {

    private final HistogramGraph2DWidget widget;

    public HistogramGraph2DSelection(HistogramGraph2DWidget widget) {
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

    @Override
    public Collection<ProcessVariable> toProcessVariables() {
        if (widget.getDataFormula() == null) {
            return null;
        }

        return Collections.singleton(new ProcessVariable(widget.getDataFormula()));
    }
}
