package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.ui.Activator;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;

/**
 * Action that removes a property.
 *
 * @author Sven Wende
 *
 */
public final class PropertyRemoveAction extends Action {

    @SuppressWarnings("unchecked")
    private AbstractPropertyContainerForm form;

    /**
     * Constructor.
     *
     * @param form
     *            a component that provides access to a property container
     */
    @SuppressWarnings("unchecked")
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
