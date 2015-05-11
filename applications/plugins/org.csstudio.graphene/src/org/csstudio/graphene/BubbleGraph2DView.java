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
public class BubbleGraph2DView extends AbstractGraph2DView<BubbleGraph2DWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.graphene.BubbleGraph2DView";

    @Override
    protected BubbleGraph2DWidget createAbstractGraph2DWidget(
        Composite parent, int style) {
    return new BubbleGraph2DWidget(parent, SWT.NONE);
    }

}
