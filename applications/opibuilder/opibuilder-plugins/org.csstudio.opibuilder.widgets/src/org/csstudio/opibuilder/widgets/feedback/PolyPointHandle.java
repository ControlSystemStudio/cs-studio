/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.ui.util.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Polyline;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.SquareHandle;
import org.eclipse.swt.graphics.Color;

/**
 * A handle, used to move points of a polyline or polygon.
 *
 * @author Sven Wende
 *
 */
public final class PolyPointHandle extends SquareHandle {
    /**
     * Index of the polygon point, that should be moved.
     */
    private int _pointIndex;

    /**
     * Creates a new Handle for the given GraphicalEditPart.
     *
     * @param owner
     *            owner of the ResizeHandle
     * @param pointIndex
     *            index of the polygon point, that should be moved
     */
    public PolyPointHandle(final GraphicalEditPart owner, final int pointIndex) {
        super();

        _pointIndex = pointIndex;
        setOwner(owner);

        PolyPointLocator locator = new PolyPointLocator((Polyline) owner
                .getFigure(), pointIndex);
        setLocator(locator);

        setCursor(Cursors.CROSS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DragTracker createDragTracker() {
        return new PolyPointDragTracker(getOwner(), _pointIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Color getBorderColor() {
        return ColorConstants.darkGray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Color getFillColor() {
        return ColorConstants.yellow;
    }
}
