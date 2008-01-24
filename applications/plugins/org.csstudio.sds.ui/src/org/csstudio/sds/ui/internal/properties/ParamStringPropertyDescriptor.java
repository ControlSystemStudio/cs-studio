package org.csstudio.sds.ui.internal.properties;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.csstudio.sds.ui.internal.properties.view.TextPropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * Descriptor for a property that has a value which should be edited with a parameterized text
 * cell editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * 
 * @author Kai Meyer
 */
public final class ParamStringPropertyDescriptor extends TextPropertyDescriptor {

	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 * @param category
	 *            the category
	 */
	public ParamStringPropertyDescriptor(final Object id, final String displayName,
			final String category) {
		super(id, displayName, category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		Map<String, WidgetProperty> properties = new HashMap<String, WidgetProperty>();
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor instanceof DisplayEditor) {
			List<AbstractBaseEditPart> editParts = ((DisplayEditor)activeEditor).getSelectedEditParts();
			if (editParts.size()!=0) {
				List<String> propertyNames = new LinkedList<String>();
				 propertyNames.addAll(editParts.get(0).getWidgetModel().getVisiblePropertyNames());
				 for (int i=1;i<editParts.size();i++) {
					 propertyNames.retainAll(editParts.get(i).getWidgetModel().getVisiblePropertyNames());
				 }
				 for (String name : propertyNames) {
					 if (!name.equals(this.getId())) {
						 properties.put(name, editParts.get(0).getWidgetModel().getProperty(name));
					 }
				 }
			}
		}
		CellEditor editor = new ParamStringCellEditor(parent, properties);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
}
