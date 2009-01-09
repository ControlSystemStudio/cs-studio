package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.ChangeNameCommand;
import org.csstudio.dct.ui.editor.tables.AbstractTableRowAdapter;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.AliasResolutionException;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.RGB;

/**
 * Row adapter for the name of the record.
 * 
 * @author Sven Wende
 * 
 */
class RecordNameTableRowAdapter extends AbstractTableRowAdapter<IRecord> {

	public RecordNameTableRowAdapter(IRecord record, CommandStack commandStack) {
		super(record, commandStack);
	}

	@Override
	protected String doGetKey(IRecord record) {
		return "Name";
	}

	@Override
	protected Object doGetValue(IRecord record) {
		return record.getNameFromHierarchy();
	}

	@Override
	protected Object doGetValueForDisplay(IRecord record) {
		String result = record.getNameFromHierarchy();

		if (record.isInherited()) {
			try {
				result = AliasResolutionUtil.resolve(record.getNameFromHierarchy(), record);
			} catch (AliasResolutionException e) {
				setError(e.getMessage());
			}
		}
		
		return result;
	}

	@Override
	protected Command doSetValue(IRecord record, Object value) {
		Command result = null;
		if (value != null && !value.equals(record.getNameFromHierarchy())) {
			result = new ChangeNameCommand(record, value.toString());
		}

		return result;
	}

	@Override
	protected RGB doGetForegroundColorForValue(IRecord record) {
		String name = record.getName();
		return (name != null && name.length() > 0) ? ColorSettings.OVERRIDDEN_RECORD_FIELD_VALUE : ColorSettings.INHERITED_RECORD_FIELD_VALUE;
	}

}