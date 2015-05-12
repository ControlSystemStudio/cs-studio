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
package org.csstudio.sds.ui.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

/**
 * A Figure that contains Layer.
 *
 * @author Sven Wende
 *
 */
public final class LayeredWidgetPane extends FreeformLayeredPane {

    /**
     * The List of IFigures, that are contained by this Pane.
     */
    private List<IFigure> _widgetFigures;

    /**
     * Show Borders?
     */
    private boolean _showBorder;

    /**
     * The bounds of the border of the display.
     */
    private Rectangle _borderBounds;

    private RectangleFigure _backgroundFigure;

    /**
     * Constructor.
     */
    public LayeredWidgetPane() {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param showBorder
     *            Show the border?
     */
    public LayeredWidgetPane(final boolean showBorder) {
        _widgetFigures = new ArrayList<IFigure>();
        _showBorder = showBorder;
        _backgroundFigure = new RectangleFigure();
        // {
        // /**
        // * {@inheritDoc}
        // */
        // @Override
        // public void paintFigure(final Graphics graphics) {
        // if (_showBorder && _borderBounds!=null) {
        // graphics.setForegroundColor(ColorConstants.darkGray);
        // graphics.setLineStyle(SWT.LINE_DASH);
        // Rectangle rectangle = this.getBounds().getCopy();
        // rectangle.width = rectangle.width-1;
        // rectangle.height = rectangle.height-1;
        // graphics.drawRectangle(rectangle);
        // }
        // }
        // };
        _backgroundFigure.setBounds(new Rectangle(0, 0, 1, 1));
        _backgroundFigure.setVisible(false);
        this.add(_backgroundFigure);
    }

    /**
     * Sets if the border of the display should be shown.
     *
     * @param showBorder
     *            True if the border should be shown, false otherwise
     */
    public void setShowBorder(final boolean showBorder) {
        _showBorder = showBorder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintFigure(final Graphics graphics) {
        super.paintFigure(graphics);
        if (_showBorder && _borderBounds != null) {
            graphics.setForegroundColor(ColorConstants.darkGray);
            graphics.setLineStyle(SWT.LINE_DASH);
            graphics.drawRectangle(_borderBounds);
        }
    }

    /**
     * Sets the bounds of the display.
     *
     * @param width
     *            The width
     * @param height
     *            The height
     */
    public void setBorderBounds(final int width, final int height) {
        _borderBounds = new Rectangle(0, 0, width, height);
        _backgroundFigure.setLocation(new Point(_borderBounds.width - 1,
                _borderBounds.height - 1));
        // _backgroundFigure.setBounds(_borderBounds);
    }

    /**
     * Sets the visibility of the Layer specified by the given layerId to
     * <i>visible</i>.
     *
     * @param layerId
     *            The id of the Layer
     * @param visible
     *            The new visibility for the Layer
     */
    public void setVisibility(final String layerId, final boolean visible) {
        Layer layer = getLayer(layerId);
        assert layer != null;
        layer.setVisible(visible);
    }

    /**
     * Move the Layer specified by the given layerId to the new index.
     *
     * @param layerId
     *            The id of the Layer
     * @param newIndex
     *            The new index for the Layer
     */
    public void moveLayer(final String layerId, final int newIndex) {
        Layer layer = getLayer(layerId);
        assert layer != null;
        remove(layer);
        // add(layer, layerId, newIndex);
        add(layer, layerId, newIndex + 1);
    }

    /**
     * Adds the given IFigure at the given index and inserts the IFigure to the
     * Layer specified by the given layerId.
     *
     * @param layerId
     *            The id of the Layer
     * @param widgetFigure
     *            The IFigure that should be added
     * @param index
     *            The index for the IFigure
     */
    @SuppressWarnings("unchecked")
    public void addWidget(final String layerId, final IFigure widgetFigure,
            final int index) {
        Layer layer = getLayer(layerId);
        if (layer == null) {
            addLayer(layerId, -1);
            layer = getLayer(layerId);
        }
        assert layer != null;

        // add the figure
        _widgetFigures.add(index, widgetFigure);

        // add the figure to the layer at the right index position

        // FIXME: Sven Wende: Optimieren der Performance bei dieser Einfügeoperation!

        List<IFigure> widgetsInLayer = layer.getChildren();

        int insertIndex = 0;

        boolean found = false;
        int cnt = index - 1;
        while (!found && cnt >= 0) {
            IFigure preceeding = _widgetFigures.get(cnt);

            int indexOfPreceedingInLayer = widgetsInLayer.indexOf(preceeding);

            if (indexOfPreceedingInLayer >= 0) {
                found = true;
                insertIndex = indexOfPreceedingInLayer + 1;
            }

            cnt--;
        }

        layer.add(widgetFigure, insertIndex);
    }

    /**
     * Removes the given IFigure.
     *
     * @param widgetFigure
     *            The IFigure that should be removed
     */
    @SuppressWarnings("unchecked")
    public void removeWidget(final IFigure widgetFigure) {
        for (Object obj : getChildren()) {
            if (obj instanceof Layer) {
                Layer layer = (Layer) obj;
                if (layer.getChildren().contains(widgetFigure)) {
                    layer.remove(widgetFigure);
                    _widgetFigures.remove(widgetFigure);
                }
            }
        }
    }

    // /**
    // * {@inheritDoc}
    // */
    // @SuppressWarnings("unchecked")
    // @Override
    // public List getChildren() {
    // List result = new LinkedList();
    // for (Object obj : super.getChildren()) {
    // if (!obj.equals(_backgroundFigure)) {
    // result.add(obj);
    // }
    // }
    // return result;
    // }

    /**
     * Creates a new FreeFormLayer.
     *
     * @return Layer The new FreeFormLayer
     */
    private Layer createFreeFormLayer() {
        Layer f = new FreeformLayer();
        f.setLayoutManager(new FreeformLayout());
        return f;
    }

    /**
     * Adds a new Layer with the given <i>layerName</i> at the given index to
     * this Pane.
     *
     * @param layerName
     *            The name of the new Layer
     * @param index
     *            The index of the new Layer
     */
    public void addLayer(final String layerName, final int index) {
        if (!hasLayer(layerName)) {
            Layer layer = createFreeFormLayer();
            add(layer, layerName, index + 1);
        }
    }

    /**
     * Removes the Layer with the given <i>layerName</i> and adds all contained
     * IFigure to the Layer with the name <i>fallBackLayerName</i>.
     *
     * @param layerName
     *            The name of the Layer, which should be removed
     * @param fallBackLayerName
     *            The name of the Layer, where all IFigure of the removed Layer
     *            should be added
     */
    @SuppressWarnings("unchecked")
    public void removeLayer(final String layerName,
            final String fallBackLayerName) {
        assert layerName != null;
        assert fallBackLayerName != null;

        Layer layer = getLayer(layerName);

        assert layer != null;

        // move all existing widget figures to the fallback layer
        List<IFigure> widgets2move = new ArrayList<IFigure>(layer.getChildren());

        for (IFigure f : widgets2move) {
            moveWidget(f, layerName, fallBackLayerName);
        }

        // remove the layer
        remove(layer);
    }

    /**
     * Moves the given IFigure from the Layer specified by the <i>oldLayerName</i>
     * to the Layer specified by the <i>newLayerName</i>.
     *
     * @param figure
     *            The IFigure, which should be moved
     * @param oldLayerName
     *            The name of the old Layer
     * @param newLayerName
     *            The name of the new Layer
     */
    public void moveWidget(final IFigure figure, final String oldLayerName,
            final String newLayerName) {
        assert figure != null;
        assert oldLayerName != null;
        assert newLayerName != null;
        Layer newLayer = getLayer(newLayerName);
        Layer oldLayer = getLayer(oldLayerName);
        assert newLayer != null;
        assert oldLayer != null;
        oldLayer.remove(figure);
        newLayer.add(figure);
    }

    /**
     * Returns true if a Layer with the given name exists.
     *
     * @param layerName
     *            The name of a Layer
     * @return boolean True if the Layer exists, false otherwise
     */
    public boolean hasLayer(final String layerName) {
        return super.getLayer(layerName) != null;
    }

}
