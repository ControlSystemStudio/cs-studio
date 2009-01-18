package org.csstudio.dct.ui.internal;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.platform.logging.CentralLogger;

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
		String name = AliasResolutionUtil.getNameFromHierarchy(record);
		
		if(record.isInherited()) {
			try {
				name = ResolutionUtil.resolve(name, record);
			} catch (AliasResolutionException e) {
				CentralLogger.getInstance().warn(this, e);
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
