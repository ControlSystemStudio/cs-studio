package org.csstudio.dct.ui.internal;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * UI adapter for {@link IRecord}.
 * 
 * @author Sven Wende
 */
public final class RecordWorkbenchAdapter extends BaseWorkbenchAdapter<IRecord> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetLabel(IRecord record) {
		String name = AliasResolutionUtil.getNameFromHierarchy(record);

		if (record.isInherited()) {
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
		Boolean disabled = AliasResolutionUtil.getPropertyViaHierarchy(record, "disabled");
		String postfix = (disabled != null && disabled) ? "_disabled" : "";
		return record.isInherited() ? "icons/record_inherited"+postfix+".png" : "icons/record"+postfix+".png";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RGB doGetForeground(IRecord record) {
		Boolean disabled = AliasResolutionUtil.getPropertyViaHierarchy(record, "disabled");
		return (disabled != null && disabled) ? new RGB(192, 192, 192) : super.doGetForeground(record);
	}
}
