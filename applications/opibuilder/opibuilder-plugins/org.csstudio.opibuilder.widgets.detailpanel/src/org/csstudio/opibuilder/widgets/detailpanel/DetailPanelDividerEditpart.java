package org.csstudio.opibuilder.widgets.detailpanel;

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Rectangle;

public class DetailPanelDividerEditpart  extends MouseMotionListener.Stub implements MouseListener {

    /* Properties */
    private enum DragState {IDLE, DRAGGING};
    DragState dragState = DragState.IDLE;
    private int mouseStart;
    private int dividerStart;
    private Rectangle containerClientArea;
    private Boolean horizontal;
    private String propertyName;

    /* Relationships */
    DetailPanelEditpart editpart;
    DetailPanelDividerFigure figure;

    /* Constructor */
    public DetailPanelDividerEditpart(DetailPanelEditpart editpart, DetailPanelDividerFigure figure,
            boolean horizontal, String propertyName) {
        this.editpart = editpart;
        this.figure = figure;
        this.horizontal = horizontal;
        this.propertyName = propertyName;
        this.figure.addMouseListener(this);
        this.figure.addMouseMotionListener(this);
    }

    /* Clean up */
    public void dispose() {
        this.figure.removeMouseListener(this);
        this.figure.removeMouseMotionListener(this);
    }

    /* The mouse button has been pressed while over the divider */
    public void mousePressed(MouseEvent me) {
        if (me.button == 1 /* left button */) {
            figure.showDivider(true);
            dragState = DragState.DRAGGING;
            containerClientArea = figure.getContainerRectangle();
            dividerStart = figure.getDividerPosition();
            if(horizontal) {
                mouseStart = me.x;
            } else {
                mouseStart = me.y;
            }
            me.consume();
        }
    }

    /* Dragging the divider */
    @Override
    public void mouseDragged(MouseEvent me) {
        if(dragState == DragState.DRAGGING) {
            // Work out new position and update the figure only
            figure.setPosition(newDividerPos(me));
            // Need to update the figure here
            me.consume();
        }
    }

    /* Calculate the new position of the divider in pixels taking account of
     * the available space.     */
    private int newDividerPos(MouseEvent me) {
        int pixelPos;
        int maxPos;
        int minPos;
        if(horizontal) {
            pixelPos = dividerStart + me.x - mouseStart;
            maxPos = containerClientArea.width() - figure.getOffset() - 3;
            minPos = 3;
        } else {
            pixelPos = dividerStart + me.y - mouseStart;
            maxPos = containerClientArea.height() - figure.getOffset() - 3;
            minPos = 3;
        }
        // Clip the position to the boundary
        if(pixelPos < minPos) {
            pixelPos = minPos;
        } else if (pixelPos > maxPos) {
            pixelPos = maxPos;
        }
        return pixelPos;
    }

    /* Ignore double clicks for now.  Possible automatic size function later? */
    @Override
    public void mouseDoubleClicked(MouseEvent me) {
    }

    /* The mouse button has been released */
    public void mouseReleased(MouseEvent me) {
        if (me.button == 1 /* left button */) {
            if(dragState == DragState.DRAGGING) {
                // End of drag operation
                dragState = DragState.IDLE;
                figure.showDivider(false);
                // Change the model
                editpart.getViewer().getEditDomain().getCommandStack().execute(
                    new SetWidgetPropertyCommand(editpart.getWidgetModel(), propertyName,
                    newDividerPos(me)));
                me.consume();
            }
        }
    }

}
