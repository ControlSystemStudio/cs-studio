/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.feedback;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

/**
 *
 * Places a handle for a polygon or polyline point. The placement is determined by
 * indicating the polyline figure to which the placement is relative, and a value
 * indicating a point index.
 *
 * @author Sven Wende
 */
public final class PolyPointLocator implements Locator {
    /**
     * The reference figure.
     */
    private Polyline _referenceFigure;

    /**
     * Index of the point, the handle should be placed for.
     */
    private int _pointIndex;

    /**
     * Constructs a poly point handle locator.
     * @param referenceFigure the reference figure ({@link Polyline} or subclasses of it)
     * @param pointIndex the index of the polygon point for which a handle should be placed
     */
    public PolyPointLocator(final Polyline referenceFigure, final int pointIndex) {
        assert referenceFigure != null;
        assert pointIndex>=0 : "pointIndex>=0"; //$NON-NLS-1$
        assert referenceFigure.getPoints().size()>pointIndex : "referenceFigure.getPoints().size()>pointIndex"; //$NON-NLS-1$
        _pointIndex = pointIndex;
        _referenceFigure = referenceFigure;
    }

    /**
     * Returns the Reference Box in the Reference Figure's coordinate system.
     * The returned Rectangle may be by reference, and should <b>not</b> be
     * modified.
     *
     * @return the reference box
     * @since 2.0
     */
    protected Rectangle getReferenceBox() {
        if (_referenceFigure instanceof HandleBounds) {
            return ((HandleBounds)_referenceFigure).getHandleBounds();
        }
        return _referenceFigure.getBounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void relocate(final IFigure target) {
        Point p = _referenceFigure.getPoints().getPoint(_pointIndex);

        _referenceFigure.translateToAbsolute(p);
        target.translateToRelative(p);
        Rectangle relativeBounds = new PrecisionRectangle(getReferenceBox()
                .getResized(-1, -1));
        Dimension targetSize = target.getPreferredSize();

        relativeBounds.x = p.x - ((targetSize.width + 1) / 2);
        relativeBounds.y = p.y - ((targetSize.height + 1) / 2);

        relativeBounds.setSize(targetSize);
        target.setBounds(relativeBounds);

    }

}
