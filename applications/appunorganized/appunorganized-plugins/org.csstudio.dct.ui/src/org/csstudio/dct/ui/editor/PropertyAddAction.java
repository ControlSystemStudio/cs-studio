package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.ui.Activator;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;

/**
 * Action that adds a property to a {@link IPropertyContainer}.
 *
 * @author Sven Wende
 */
public class PropertyAddAction extends Action {
    @SuppressWarnings("unchecked")
    private AbstractPropertyContainerForm form;

    /**
     * Constructor.
     *
     * @param form
     *            a component that provides access to a property container
     */
    @SuppressWarnings("unchecked")
    public PropertyAddAction(AbstractPropertyContainerForm form) {
        assert form != null;
        this.form = form;

        setText("Add Property");
        setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/parameter_add.png"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        IPropertyContainer c = (IPropertyContainer)form.getInput();
        if (c != null) {
            c.addProperty("new property", "");
            form.refresh();
        }
    }
}
