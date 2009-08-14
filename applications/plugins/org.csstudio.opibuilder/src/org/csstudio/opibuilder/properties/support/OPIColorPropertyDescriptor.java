package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.visualparts.OPIColorCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;

public class OPIColorPropertyDescriptor extends ColorPropertyDescriptor {

	public OPIColorPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		setLabelProvider(new WorkbenchLabelProvider());
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		OPIColorCellEditor editor = new OPIColorCellEditor(parent, "Choose Color");
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
}
