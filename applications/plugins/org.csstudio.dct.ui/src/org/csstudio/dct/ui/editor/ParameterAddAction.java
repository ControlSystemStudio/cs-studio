package org.csstudio.dct.ui.editor;

import org.csstudio.dct.ui.Activator;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;

/**
 * Popup menu action for the parameter table that adds a new parameter.
 *
 * @author Sven Wende
 *
 */
public final class ParameterAddAction extends Action {
    private PrototypeForm form;

    /**
     * Constructor.
     *
     * @param form
     *            the prototype form
     */
    public ParameterAddAction(PrototypeForm form) {
        assert form != null;
        this.form = form;
        setText("Add Parameter");
        setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/parameter_add.png"));
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void run() {
        form.addParameter();
    }
}
