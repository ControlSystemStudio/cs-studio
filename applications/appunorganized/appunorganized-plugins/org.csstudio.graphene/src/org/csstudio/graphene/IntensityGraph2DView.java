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
public class IntensityGraph2DView extends AbstractGraph2DView<IntensityGraph2DWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.graphene.IntensityGraph2DView";

    @Override
    protected IntensityGraph2DWidget createAbstractGraph2DWidget(
        Composite parent, int style) {
    return new IntensityGraph2DWidget(parent, SWT.NONE);
    }

}
