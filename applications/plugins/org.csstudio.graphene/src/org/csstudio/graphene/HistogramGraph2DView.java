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
public class HistogramGraph2DView extends AbstractGraph2DView<HistogramGraph2DWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.graphene.HistogramGraph2DView";

    @Override
    protected HistogramGraph2DWidget createAbstractGraph2DWidget(
        Composite parent, int style) {
        return new HistogramGraph2DWidget(parent, SWT.NONE);
    }

}
