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
package org.csstudio.sds.ui.figures;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ZoomManager;

/**
 * A Widget, which links to another display.
 *
 * @author Sven Wende
 *
 */
public final class LinkingContainerFigure extends Panel implements IAdaptable {

    /**
     * The content pane of this widget.
     */
    private final ScalableFreeformLayeredPane _pane;
    /**
     * The zoom manager for this widget.
     */
    private final ZoomManager _zoomManager;

    private final FreeformViewport _freeformViewport;
    private boolean autoFit;

    private final ScrollPane scrollpane;
    private CrossedOutAdapter _crossedOutAdapter;
    private IRhombusEquippedWidget _rhombusAdapter;
    /**
     * Constructor.
     */
    public LinkingContainerFigure() {
        final XYLayout layout = new XYLayout();
        setLayoutManager(layout);

        scrollpane = new ScrollPane();
        scrollpane.setScrollBarVisibility(ScrollPane.NEVER);
        _freeformViewport = new FreeformViewport();
        _freeformViewport.setSize(40,40);
        scrollpane.setViewport(_freeformViewport);
        add(scrollpane);

        _pane = new ScalableFreeformLayeredPane();
        _pane.setLayoutManager(new FreeformLayout());

        scrollpane.setContents(_pane);

        _zoomManager = new ZoomManager(_pane, _freeformViewport);

        setForegroundColor(ColorConstants.blue);
        setOpaque(true);

        addFigureListener(new FigureListener() {
            public void figureMoved(final IFigure source) {
                updateChildConstraints();
                updateZoom();
            }

        });

        updateChildConstraints();
        updateZoom();
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);
    }

    /**
     *
     *
     */
    private void updateChildConstraints() {
        final Rectangle figureBounds = getBounds();

        Rectangle r = new Rectangle(0, 0, figureBounds.width
                , figureBounds.height);

        _freeformViewport.setSize(r.width, r.height);
        _pane.setSize(new Dimension(r.width, r.height));
        setConstraint(scrollpane, r);
    }


    /**
     * Returns the content pane.
     *
     * @return IFigure The content pane.
     */
    public LayeredPane getContentsPane() {
        return _pane;
    }

    /**
     * Refreshes the zoom.
     */
    public void updateZoom() {
        _zoomManager.setZoom(1.0);

        if (autoFit) {
            _zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean useLocalCoordinates() {
        return true;
    }

    /**
     * This method is a tribute to unit tests, which need a way to test the
     * performance of the figure implementation. Implementors should produce
     * some random changes and refresh the figure, when this method is called.
     *
     */
    public void randomNoiseRefresh() {
        // nothing to do yet
    }

    public void setAutoFit(final boolean autoFit) {
        this.autoFit = autoFit;
        updateZoom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            return new BorderAdapter(this);
        }else  if (adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        }else  if (adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }
        return null;

    }

}
