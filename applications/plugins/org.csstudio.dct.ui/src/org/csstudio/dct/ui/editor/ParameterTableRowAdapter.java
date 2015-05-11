package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.Activator;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;

/**
 * Table adapter for Parameters.
 *
 * @author Sven Wende
 *
 */
public final class ParameterTableRowAdapter extends AbstractTableRowAdapter<Parameter> {

    /**
     * Constructor.
     *
     * @param parameter
     *            a parameter
     */
    public ParameterTableRowAdapter(Parameter parameter) {
        super(parameter);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected boolean doCanModifyKey(Parameter parameter) {
        return true;
    }
    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetKey(Parameter parameter) {
        return parameter.getName();
    }
    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetValue(Parameter parameter) {
        return parameter.getDefaultValue();
    }
    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetValueForDisplay(Parameter parameter) {
        return parameter.getDefaultValue();
    }
    /**
     *{@inheritDoc}
     */
    @Override
    protected Command doSetValue(Parameter parameter, Object value) {
        parameter.setDefaultValue(value.toString());
        // FIXME: Command liefern!
        return null;

    }
    /**
     *{@inheritDoc}
     */
    @Override
    protected Image doGetImage(Parameter delegate,int columnIndex) {
        return columnIndex==0?CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/parameter.png"):null;
    }

}
