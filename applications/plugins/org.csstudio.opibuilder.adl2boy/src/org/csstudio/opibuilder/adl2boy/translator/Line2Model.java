package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.eclipse.swt.graphics.RGB;

public class Line2Model extends PolyLine2Model {

	public Line2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}
}
