package org.csstudio.dct.ui.graphicalviewer.model;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.util.AliasResolutionUtil;

/**
 * Box node that represents {@link IInstance}s in the graphical model.
 *
 * @author Sven Wende
 *
 */
public class InstanceNode extends AbstractContainerNode<IInstance> {
    /**
     * Standard constructor.
     *
     * @param instance
     *            the dct instance that is represented graphically
     */
    public InstanceNode(IInstance instance) {
        super(instance);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetCaption(IInstance instance) {
        return AliasResolutionUtil.getNameFromHierarchy(instance);
    }

}
