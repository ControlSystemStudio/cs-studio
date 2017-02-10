package org.csstudio.opibuilder.widgets.detailpanel;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class DetailPanelDividerFigure extends Figure {

    /* Drawing information */
    boolean horizontal;
    int position;
    int offset;
    Figure container;
    boolean showDivider;

    /* Colours */
    private final static Color BORDER_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_DARK_GRAY);

    /* Constructor */
    public DetailPanelDividerFigure(Figure container, boolean horizontal) {
        // Record information
        this.horizontal = horizontal;
        this.container = container;
        this.position = 50;
        this.offset = 0;
        this.showDivider = false;
        // Set the cursor that is shown
        if (horizontal) {
            setCursor(Cursors.SIZEWE);
        }
        else {
            setCursor(Cursors.SIZENS);
        }
    }

    /* Clean up */
    public void dispose() {
    }

    /* Return the current divider position in pixels */
    public int getDividerPosition() {
        return position;
    }

    /* Set the offset of the divider */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /* Return the current offset of the divider */
    public int getOffset() {
        return offset;
    }

    /* The divider position has changed */
    public void setPosition(int pos) {
        position = pos;
        revalidate();
        repaint();
    }

    /* Show or hide the divider.  We only show the divider while it
     * is being dragged. */
    public void showDivider(boolean show) {
        showDivider = show;
    }

    /* Draw the divider */
    @Override
    protected void paintFigure(Graphics graphics) {
        super.paintFigure(graphics);
        if(showDivider) {
            Rectangle bounds = getBounds();
            graphics.pushState();
            graphics.setForegroundColor(BORDER_COLOR);
            if(horizontal) {
                graphics.drawLine(bounds.x+1, bounds.y, bounds.x+1, bounds.y+bounds.height);
            } else {
                graphics.drawLine(bounds.x, bounds.y+1, bounds.x+bounds.width, bounds.y+1);
            }
            graphics.popState();
        }
    }

    /* Layout the divider */
    @Override
    protected void layout() {
        Rectangle containerBounds = container.getClientArea();
        if(horizontal) {
            setBounds(new Rectangle(containerBounds.x + position + offset - 1,
                    containerBounds.y, 3, containerBounds.height));
        } else {
            setBounds(new Rectangle(containerBounds.x,
                    containerBounds.y + position + offset - 1,
                    containerBounds.width, 3));
        }
    }

    /* Return the rectangle that contains the divider */
    public Rectangle getContainerRectangle() {
        return container.getClientArea();
    }
}
