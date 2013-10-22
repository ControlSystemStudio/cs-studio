package org.csstudio.utility.pvmanager.widgets;

import java.util.Collection;
import java.util.Collections;

import org.csstudio.csdata.ProcessVariable;
import org.epics.vtype.VType;

public class VImageWidgetSelection implements VTypeAdaptable, ProcessVariableAdaptable {
	private final VImageWidget vImageWidget;
	
	public VImageWidgetSelection(VImageWidget vImageWidget) {
		this.vImageWidget = vImageWidget;
	}

	@Override
	public VType toVType() {
		return vImageWidget.getValue();
	}

	@Override
	public Collection<ProcessVariable> toProcessVariables() {
		return Collections.singleton(new ProcessVariable(vImageWidget.getPvFormula()));
	}
}
