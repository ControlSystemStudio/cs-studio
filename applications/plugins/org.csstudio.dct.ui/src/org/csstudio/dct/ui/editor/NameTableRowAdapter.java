package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.csstudio.dct.ui.editor.tables.AbstractTableRowAdapter;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.RGB;

/**
 * Row adapter for the name of the record.
 * 
 * @author Sven Wende
 * 
 */
class NameTableRowAdapter extends AbstractTableRowAdapter<IElement> {

	public NameTableRowAdapter(IElement element, CommandStack commandStack) {
		super(element, commandStack);
	}

	@Override
	protected String doGetKey(IElement element) {
		return "Name";
	}

	@Override
	protected Object doGetValue(IElement element) {
		return AliasResolutionUtil.getNameFromHierarchy(element);
	}

	@Override
	protected Object doGetValueForDisplay(IElement element) {
		String result =AliasResolutionUtil.getNameFromHierarchy(element);

		if (element.isInherited()) {
			try {
				result = ResolutionUtil.resolve(result, element);
			} catch (AliasResolutionException e) {
				setError(e.getMessage());
			}
		}
		
		return result;
	}

	@Override
	protected Command doSetValue(IElement element, Object value) {
		Command result = null;
		if (value != null && !value.equals(AliasResolutionUtil.getNameFromHierarchy(element))) {
			result = new ChangeBeanPropertyCommand(element, "name", value.toString());
		}

		return result;
	}

	@Override
	protected RGB doGetForegroundColorForValue(IElement element) {
		String name = element.getName();
		return (name != null && name.length() > 0) ? ColorSettings.OVERRIDDEN_VALUE : ColorSettings.INHERITED_VALUE;
	}

}
