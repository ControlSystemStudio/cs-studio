package org.csstudio.utility.pvmanager.widgets;

import java.util.Collection;
import java.util.Collections;

import org.csstudio.csdata.ProcessVariable;
import org.epics.vtype.VType;

public class VTableWidgetSelection implements VTypeAdaptable, ProcessVariableAdaptable {
    private final VTableWidget vTableWidget;

    public VTableWidgetSelection(VTableWidget vTableWidget) {
        this.vTableWidget = vTableWidget;
    }

    @Override
    public VType toVType() {
        return vTableWidget.getValue();
    }

    @Override
    public Collection<ProcessVariable> toProcessVariables() {
        return Collections.singleton(new ProcessVariable(vTableWidget.getPvFormula()));
    }
}
