package org.csstudio.dct.ui.graphicalviewer.model;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;

/**
 * Node that represents {@link IRecord}s in the graphical model.
 *
 * @author Sven Wende
 *
 */
public class RecordNode extends AbstractNode<IRecord> {

    /**
     * Standard constructor.
     *
     * @param record
     *            the dct record that is represented graphically
     */
    public RecordNode(IRecord record) {
        super(record);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetCaption(IRecord record) {
        StringBuffer sb = new StringBuffer();

        if (record.isAbstract()) {
            sb.append(AliasResolutionUtil.getNameFromHierarchy(record));
        } else {
            String name = AliasResolutionUtil.getEpicsNameFromHierarchy(record);
            String resolvedName;
            try {
                resolvedName = ResolutionUtil.resolve(name, record);
                sb.append(resolvedName);
            } catch (AliasResolutionException e) {
                sb.append(AliasResolutionUtil.getNameFromHierarchy(record));
            }
        }
        sb.append(" [" + record.getType() + "]");

        return sb.toString();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visit(this);
    }

}
