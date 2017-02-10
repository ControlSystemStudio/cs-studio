package org.csstudio.opibuilder.widgets.detailpanel;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
//import org.eclipse.draw2d.geometry.Rectangle;

public class DetailPanelDraggerEditpart extends MouseMotionListener.Stub implements MouseListener {

    /* Properties */
    private enum DragState {IDLE, DRAGGING};
    DragState dragState = DragState.IDLE;

    /* Relationships */
    DetailPanelEditpart editpart;
    DetailPanelDraggerFigure dragger;
    DetailPanelDividerFigure divider;
    int rowNumber;

    /* Constructor */
    public DetailPanelDraggerEditpart(DetailPanelEditpart editpart, DetailPanelDraggerFigure dragger,
            DetailPanelDividerFigure divider, int rowNumber) {
        this.editpart = editpart;
        this.dragger = dragger;
        this.divider = divider;
        this.rowNumber = rowNumber;
        this.dragger.addMouseListener(this);
        this.dragger.addMouseMotionListener(this);
    }

    /* Clean up */
    public void dispose() {
        this.dragger.removeMouseListener(this);
        this.dragger.removeMouseMotionListener(this);
    }

    /* The mouse button has been pressed while over the dragger */
    @Override
    public void mousePressed(MouseEvent me) {
        if (me.button == 1 /* left button */) {
            dragState = DragState.DRAGGING;
            divider.setVisible(true);
            divider.showDivider(true);
            divider.setPosition(me.y);
            me.consume();
        }
    }

    /* Dragging the row */
    @Override
    public void mouseDragged(MouseEvent me) {
        if(dragState == DragState.DRAGGING) {
            // Work out new position and update the figure only
            divider.setPosition(me.y);
            me.consume();
        }
    }

    /* Ignore double clicks for now. */
    @Override
    public void mouseDoubleClicked(MouseEvent me) {
    }

    /* The mouse button has been released */
    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.button == 1 /* left button */) {
            if(dragState == DragState.DRAGGING) {
                // End of drag operation
                dragState = DragState.IDLE;
                divider.showDivider(false);
                divider.setVisible(false);
                // Find the row boundary the drag finished nearest
                int moveTo = editpart.getFigure().findNearestRowBoundary(me.y);
                // Does this mean this row must change its index?
                if(moveTo != rowNumber && moveTo != rowNumber+1) {
                    // Create a command object to move the row
                    editpart.getViewer().getEditDomain().getCommandStack().execute(
                        new DetailPanelChangeRowIndexCommand(editpart, rowNumber, moveTo));
                }
                me.consume();
            }
        }
    }
}
