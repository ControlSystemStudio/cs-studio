package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.commands.ChangeParameterValueCommand;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.RGB;

/**
 * Table adapter for parameter values.
 *
 * @author Sven Wende
 *
 */
public final class ParameterValueTableRowAdapter extends AbstractTableRowAdapter<IInstance> {
    private Parameter parameter;

    /**
     * Constructor.
     *
     * @param instance
     *            an instance
     * @param parameter
     *            a parameter
     */
    public ParameterValueTableRowAdapter(IInstance instance, Parameter parameter) {
        super(instance);
        this.parameter = parameter;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetKey(IInstance instance) {
        return parameter.getName();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetValue(IInstance instance) {
        return AliasResolutionUtil.getParameterValueFromHierarchy(instance, parameter.getName());
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected Command doSetValue(IInstance instance, Object value) {
        return new ChangeParameterValueCommand(instance, parameter.getName(), value != null ? value.toString() : null);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected RGB doGetForegroundColorForValue(IInstance instance) {
        String key = parameter.getName();
        return instance.hasParameterValue(key) ? ColorSettings.OVERRIDDEN_PARAMETER_VALUE : ColorSettings.INHERITED_PARAMETER_VALUE;
    }

}
