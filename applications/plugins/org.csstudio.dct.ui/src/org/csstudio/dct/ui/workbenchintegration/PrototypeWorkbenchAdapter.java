package org.csstudio.dct.ui.workbenchintegration;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IPrototype;

/**
 * UI adapter for {@link IPrototype}.
 *
 * @author Sven Wende
 */
@SuppressWarnings("unchecked")
public final class PrototypeWorkbenchAdapter extends BaseWorkbenchAdapter<IPrototype> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[] doGetChildren(IPrototype prototype) {
        List list = new ArrayList();
        list.addAll(prototype.getInstances());
        list.addAll(prototype.getRecords());
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetLabel(IPrototype prototype) {
        return prototype.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetIcon(IPrototype prototype) {
        return "icons/prototype.png";
    }



}
