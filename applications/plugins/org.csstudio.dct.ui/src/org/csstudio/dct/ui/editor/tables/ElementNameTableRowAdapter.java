package org.csstudio.dct.ui.editor.tables;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.commands.ChangeNameCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;

/**
 * Row adapter for the name of the record.
 * 
 * @author Sven Wende
 * 
 */
class ElementNameTableRowAdapter extends AbstractTableRowAdapter<IElement> {

	public ElementNameTableRowAdapter(IElement delegate, CommandStack commandStack) {
		super(delegate, commandStack);
	}

	@Override
	protected String doGetKey(IElement delegate) {
		return "Name";
	}

	@Override
	protected Object doGetValue(IElement delegate) {
		return delegate.getName();
	}

	@Override
	protected Object doGetValueForDisplay(IElement delegate) {
		return delegate.getName();
	}

	@Override
	protected Command doSetValue(IElement delegate, Object value) {
		Command result = null;

		if (value != null) {
			result = new ChangeNameCommand(delegate, value.toString());
		}

		return result;
	}

}