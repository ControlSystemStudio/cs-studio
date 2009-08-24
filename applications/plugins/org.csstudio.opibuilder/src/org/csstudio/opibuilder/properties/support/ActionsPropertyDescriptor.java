package org.csstudio.opibuilder.properties.support;


import org.csstudio.opibuilder.visualparts.ActionsCellEditor;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**The property descriptor for actions property.
 * @author Xihui Chen
 *
 */
public class ActionsPropertyDescriptor extends TextPropertyDescriptor {
	
	
	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public ActionsPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);
		this.setLabelProvider(new ActionsLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new ActionsCellEditor(parent, "Set Actions");
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	/**
	 * A label provider for a {@link ActionsInput}.
	 * 
	 * @author Xihui Chen
	 */
	private final class ActionsLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof ActionsInput) {
				ActionsInput input = (ActionsInput)element;
				if(input.getActionsList().size() ==0){
					return "no action";
				}
				if(input.getActionsList().size() == 1){
					return input.getActionsList().get(0).getDescription();
				}
				return input.getActionsList().size() + " actions";
			} else {
				return element.toString();
			}
		}

	}
}
