package org.csstudio.dct.ui.workbenchintegration;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UI adapter for {@link IRecord}.
 *
 * @author Sven Wende
 */
public final class RecordWorkbenchAdapter extends BaseWorkbenchAdapter<IRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(RecordWorkbenchAdapter.class);

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
                LOG.warn("Warn", e);
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
