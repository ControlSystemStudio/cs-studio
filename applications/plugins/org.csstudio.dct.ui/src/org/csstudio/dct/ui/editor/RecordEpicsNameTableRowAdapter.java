package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.RGB;

/**
 * Row adapter for the name of the record.
 * 
 * @author Sven Wende
 * 
 */
class RecordEpicsNameTableRowAdapter extends AbstractTableRowAdapter<IRecord> {

	public RecordEpicsNameTableRowAdapter(IRecord record) {
		super(record);
	}

	@Override
	protected String doGetKey(IRecord record) {
		return "EPICS Name";
	}

	@Override
	protected String doGetValue(IRecord record) {
		return AliasResolutionUtil.getEpicsNameFromHierarchy(record);
	}

	@Override
	protected String doGetValueForDisplay(IRecord record) {
		String result = AliasResolutionUtil.getEpicsNameFromHierarchy(record);

		if (record.isInherited()) {
			try {
				result = ResolutionUtil.resolve(result, record);
			} catch (AliasResolutionException e) {
				setError(e.getMessage());
			}
		}
		
		return result;
	}

	@Override
	protected Command doSetValue(IRecord record, Object value) {
		Command result = null;
		if (value != null && !value.equals(record.getEpicsNameFromHierarchy())) {
			result = new ChangeBeanPropertyCommand(record, "epicsName", value.toString());
		}

		return result;
	}

	@Override
	protected RGB doGetForegroundColorForValue(IRecord record) {
		String name = record.getEpicsName();
		return (name != null && name.length() > 0) ? ColorSettings.OVERRIDDEN_VALUE : ColorSettings.INHERITED_VALUE;
	}

}
