package org.csstudio.dct.ui.editor;

import org.csstudio.dct.ui.Activator;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;

/**
 * Popup menu action for the parameter table that removes a selected parameter.
 *
 * @author Sven Wende
 *
 */
public final class ParameterRemoveAction extends Action {
    private PrototypeForm form;

    /**
     * Constructor.
     *
     * @param form
     *            the prototype form
     */
    public ParameterRemoveAction(PrototypeForm form) {
        assert form != null;
        this.form = form;

        setText("Remove Parameter");
        setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/parameter_remove.png"));
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void run() {
        form.removeParameter();
    }
}
