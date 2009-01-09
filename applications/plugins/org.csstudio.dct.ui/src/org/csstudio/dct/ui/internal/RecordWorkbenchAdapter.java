package org.csstudio.dct.ui.internal;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.AliasResolutionException;

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
		String name = record.getNameFromHierarchy();
		
		if(record.isInherited()) {
			try {
				name = AliasResolutionUtil.resolve(record.getNameFromHierarchy(), record);
			} catch (AliasResolutionException e) {
			}
		}
		
		return name + " [" + record.getType() + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetIcon(IRecord record) {
		return record.isInherited() ? "icons/record_inherited.png" : "icons/record.png";
	}

}
