package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.ui.Activator;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;

/**
 * Action that removes a property.
 * 
 * @author Sven Wende
 * 
 */
public class PropertyRemoveAction extends Action {
	
	private AbstractPropertyContainerForm form;

	/**
	 * Constructor.
	 * 
	 * @param form
	 *            a component that provides access to a property container
	 */
	public PropertyRemoveAction(AbstractPropertyContainerForm form) {
		assert form != null;
		this.form = form;

		setText("Remove Property");
		setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/parameter_remove.png"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		IPropertyContainer c =  (IPropertyContainer)form.getInput();
		String property = form.getSelectedProperty();

		if (c != null && property != null) {
			c.removeProperty(property);
			form.refresh();
		}
	}
}
