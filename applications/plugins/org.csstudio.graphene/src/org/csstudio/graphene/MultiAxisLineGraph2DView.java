/**
 *
 */
package org.csstudio.graphene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class MultiAxisLineGraph2DView extends AbstractGraph2DView<MultiAxisLineGraph2DWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.graphene.LineGraph2DView";

    @Override
    protected MultiAxisLineGraph2DWidget createAbstractGraph2DWidget(Composite parent,
        int style) {
    return new MultiAxisLineGraph2DWidget(parent, SWT.NONE);
    }
}
