package org.csstudio.dct.ui.workbenchintegration;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.util.AliasResolutionUtil;

/**
 * UI adapter for {@link IInstance}.
 *
 * @author Sven Wende
 */
@SuppressWarnings("unchecked")
public final class InstanceWorkbenchAdapter extends BaseWorkbenchAdapter<IInstance> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[] doGetChildren(IInstance instance) {
        List list = new ArrayList();
        list.addAll(instance.getInstances());
        list.addAll(instance.getRecords());
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetLabel(IInstance instance) {
        return AliasResolutionUtil.getNameFromHierarchy(instance) + " [" + instance.getPrototype().getName()+"]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetIcon(IInstance instance) {
        return instance.getParent() instanceof IInstance ? "icons/instance_inherited.png" : "icons/instance.png";
    }



}
