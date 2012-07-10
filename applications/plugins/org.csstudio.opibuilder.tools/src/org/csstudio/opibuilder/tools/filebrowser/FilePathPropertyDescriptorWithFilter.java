package org.csstudio.opibuilder.tools.filebrowser;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.support.FilePathPropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * File path property descriptor with filters on image resource name.
 * 
 * @author SOPRA Group
 * 
 */
public class FilePathPropertyDescriptorWithFilter extends
		FilePathPropertyDescriptor {

	private String[] filters;

	private AbstractWidgetModel widgetModel;

	public FilePathPropertyDescriptorWithFilter(Object id, String displayName,
			AbstractWidgetModel widgetModel, String[] filters) {
		super(id, displayName, widgetModel, filters);
		this.filters = filters;
		this.widgetModel = widgetModel;
	}

	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new FilePathCellDialogEditorWithFilter(parent,
				widgetModel, filters);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
