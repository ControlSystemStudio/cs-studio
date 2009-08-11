package org.csstudio.opibuilder.properties.support;


import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.visualparts.ScriptsInputCellEditor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


public class ScriptPropertyDescriptor extends TextPropertyDescriptor {
	
	
	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public ScriptPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);
		this.setLabelProvider(new ScriptsInputLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new ScriptsInputCellEditor(parent, "Attach Scripts");
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	/**
	 * A label provider for a {@link ScriptsInput}.
	 * 
	 * @author Xihui Chen
	 */
	private final class ScriptsInputLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public String getText(final Object element) {
			if (element instanceof ScriptsInput) {
				ScriptsInput input = (ScriptsInput)element;
				if(input.getScriptList().size() ==0){
					return "no script attached";
				}
				if(input.getScriptList().size() == 1){
					return input.getScriptList().get(0).getPath().toString();
				}
				return input.getScriptList().size() + " scripts attached";
			} else {
				return element.toString();
			}
		}

	}
}
