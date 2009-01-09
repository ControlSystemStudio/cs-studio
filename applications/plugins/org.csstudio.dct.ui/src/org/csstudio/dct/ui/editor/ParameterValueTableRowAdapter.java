package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.commands.ChangeParameterValueCommand;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.editor.tables.AbstractTableRowAdapter;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.RGB;

public class ParameterValueTableRowAdapter extends AbstractTableRowAdapter<IInstance>{
	private Parameter parameter;
	
	public ParameterValueTableRowAdapter(IInstance instance, Parameter parameter, CommandStack commandStack) {
		super(instance, commandStack);
		this.parameter = parameter;
	}

	@Override
	protected String doGetKey(IInstance instance) {
		return parameter.getName();
	}

	@Override
	protected Object doGetValue(IInstance instance) {
		return AliasResolutionUtil.getParameterValueFromHierarchy(instance, parameter.getName());
	}

	@Override
	protected Command doSetValue(IInstance instance, Object value) {
		return new ChangeParameterValueCommand(instance, parameter.getName(), value!=null?value.toString():null);
	}

	@Override
	protected RGB doGetForegroundColorForValue(IInstance instance) {
		String key = parameter.getName();
		return instance.hasParameterValue(key) ? ColorSettings.OVERRIDDEN_PARAMETER_VALUE : ColorSettings.INHERITED_PARAMETER_VALUE;
	}

	
}
