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
public class LineGraph2DView extends AbstractGraph2DView<LineGraph2DWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.graphene.LineGraph2DView";

    @Override
    protected LineGraph2DWidget createAbstractGraph2DWidget(Composite parent,
        int style) {
    return new LineGraph2DWidget(parent, SWT.NONE);
    }
}
