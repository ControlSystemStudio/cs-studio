package org.csstudio.dct.ui.internal;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.RecordUtil;

/**
 * UI adapter for {@link IRecord}.
 * 
 * @author Sven Wende
 */
public class RecordWorkbenchAdapter extends BaseWorkbenchAdapter<IRecord> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetLabel(IRecord record) {
		return record.isInheritedFromPrototype() ? RecordUtil.getResolvedName(record) : record.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetIcon(IRecord record) {
		return record.isInheritedFromPrototype() ? "icons/record_inherited.png" : "icons/record.png";
	}

}
