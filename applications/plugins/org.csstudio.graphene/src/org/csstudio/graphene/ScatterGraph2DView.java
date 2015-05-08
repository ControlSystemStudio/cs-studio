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
public class ScatterGraph2DView extends AbstractGraph2DView<ScatterGraph2DWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.graphene.ScatterGraph2DView";

    @Override
    protected ScatterGraph2DWidget createAbstractGraph2DWidget(
        Composite parent, int style) {
    return new ScatterGraph2DWidget(parent, SWT.NONE);
    }

}
