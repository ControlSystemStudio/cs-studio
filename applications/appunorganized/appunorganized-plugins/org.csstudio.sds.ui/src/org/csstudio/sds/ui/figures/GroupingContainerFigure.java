/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.csstudio.sds.ui.figures;

import org.csstudio.sds.ui.editparts.LayeredWidgetPane;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

/**
 * A Widget, which contains other widgets.
 * @author Sven Wende
 *
 */
public final class GroupingContainerFigure extends Figure implements HandleBounds, IAdaptable{

    /**
     * The content pane of this widget.
     */
    private final LayeredWidgetPane _pane;

    /**
     * The transparent state of the background.
     */
    private boolean _transparent = false;

    /**
     * The internal {@link ScrollPane}.
     */
    private final InternalScrollPane _scrollPane;

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    private ICrossedFigure _crossedOutAdapter;

    private IRhombusEquippedWidget _rhombusAdapter;

    /**
     * Constructor.
     */
    public GroupingContainerFigure() {
        setBorder(new LineBorder(1));
        _scrollPane = new InternalScrollPane();
        _pane = new LayeredWidgetPane();
        _pane.setLayoutManager(new FreeformLayout());
        setLayoutManager(new StackLayout());
        add(_scrollPane);
        _scrollPane.setViewport(new FreeformViewport());
        _scrollPane.setContents(_pane);

        setOpaque(true);
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);
    }

    /**
     * Returns the content pane.
     * @return IFigure
     *             The content pane
     */
    public LayeredPane getContentsPane() {
        return _pane;
    }

    /**
     * Returns the bounds of the handles.
     * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
     * @return Rectangle
     *             The bounds of the handles
     */
    @Override
    public Rectangle getHandleBounds() {
        return getBounds().getCropped(new Insets(2, 0, 2, 0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize(final int w, final int h) {
        Dimension prefSize = super.getPreferredSize(w, h);
        Dimension defaultSize = new Dimension(100, 100);
        prefSize.union(defaultSize);
        return prefSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintFigure(final Graphics graphics) {
        if (!_transparent) {
            Rectangle rect = getBounds().getCopy();
            rect.crop(new Insets(2, 0, 2, 0));
            //graphics.fillRectangle(rect);
            graphics.fillOval(rect);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CircuitBoardFigure"; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean useLocalCoordinates() {
        return false;
    }

    /**
     * This method is a tribute to unit tests, which need a way to test the
     * performance of the figure implementation. Implementors should produce
     * some random changes and refresh the figure, when this method is called.
     *
     */
    public void randomNoiseRefresh() {
        //nothing to do yet
    }

    /**
     * Gets the transparent state of the background.
     *
     * @return the transparent state of the background
     */
    public boolean gettransparent() {
        return _transparent;
    }

    /**
     * Sets the transparent state of the background.
     *
     * @param transparent
     *            the transparent state.
     */
    public void setTransparent(final boolean transparent) {
        _transparent = transparent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if (adapter == ICrossedFigure.class) {
            if (_crossedOutAdapter == null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;

        } else if (adapter == IRhombusEquippedWidget.class) {
            if (_rhombusAdapter == null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;

        }
        return null;
    }

    /**
     * A {@link ScrollPane} for internal use.
     * @author Kai Meyer
     *
     */
    private final class InternalScrollPane extends ScrollPane {
        /**
         * {@inheritDoc}
         */
        @Override
        protected void paintFigure(final Graphics graphics) {
            if (!_transparent) {
                super.paintFigure(graphics);
            }
        }
    }

}
