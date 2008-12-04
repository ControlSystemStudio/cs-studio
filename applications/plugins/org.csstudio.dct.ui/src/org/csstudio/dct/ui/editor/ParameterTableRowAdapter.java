package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.Activator;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.Image;

public class ParameterTableRowAdapter extends AbstractTableRowAdapter<Parameter>{

	public ParameterTableRowAdapter(Parameter parameter, CommandStack commandStack) {
		super(parameter, commandStack);
	}

	@Override
	protected boolean doCanModifyKey(Parameter parameter) {
		return true;
	}

	@Override
	protected String doGetKey(Parameter parameter) {
		return parameter.getName();
	}

	@Override
	protected Object doGetValue(Parameter parameter) {
		return parameter.getDefaultValue();
	}

	@Override
	protected Object doGetValueForDisplay(Parameter parameter) {
		return parameter.getDefaultValue();
	}

	@Override
	protected Command doSetKey(Parameter parameter, String key) {
		parameter.setName(key);
		// FIXME: Command liefern!
		return null;
	}

	@Override
	protected Command doSetValue(Parameter parameter, Object value) {
		parameter.setDefaultValue(value.toString());
		// FIXME: Command liefern!
		return null;

	}

	@Override
	protected Image doGetImage(Parameter delegate) {
		return CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/parameter.png");
	}

	
}
