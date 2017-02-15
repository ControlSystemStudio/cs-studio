package org.csstudio.opibuilder.widgets.detailpanel;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.swt.SWT;

public class DetailPanelEditpartRow {

    public class GroupTriangleListener extends MouseMotionListener.Stub implements MouseListener {
        /* Do nothing with this event. */
        @Override
        public void mouseDoubleClicked(MouseEvent arg0) {
        }

        /* The group triangle has been pressed, change the
         * group collapse state.
         */
        @Override
        public void mousePressed(MouseEvent arg0) {
            editpart.setGroupCollapse(figure.getRowNumber(), !figure.getGroupCollapse());
        }

        /* Do nothing with this event. */
        @Override
        public void mouseReleased(MouseEvent arg0) {
        }

        /* The user has moved the mouse over the group triangle,
         * give some feedback.
         */
        @Override
        public void mouseEntered(MouseEvent me) {
            super.mouseEntered(me);
            figure.setHighlightGroupTriangle(true);
        }

        /* The user has moved the mouse off the group triangle,
         * give some feedback.
         */
        @Override
        public void mouseExited(MouseEvent me) {
            super.mouseExited(me);
            figure.setHighlightGroupTriangle(false);
        }
    }

    public class NameLabelListener extends MouseMotionListener.Stub implements MouseListener {
        /* Do nothing with this event. */
        @Override
        public void mouseDoubleClicked(MouseEvent arg0) {
        }

        /* The name label has been clicked, select the row*/
        @Override
        public void mousePressed(MouseEvent e) {
            figure.select((e.getState() & SWT.SHIFT) != 0, e.button == 3);
        }

        /* Do nothing with this event. */
        @Override
        public void mouseReleased(MouseEvent arg0) {
        }

        /* The user has moved the mouse over the name label.*/
        @Override
        public void mouseEntered(MouseEvent me) {
        }

        /* The user has moved the mouse off the name label.*/
        @Override
        public void mouseExited(MouseEvent me) {
        }

    }

    private DetailPanelDividerEditpart divider;
    private DetailPanelDraggerEditpart dragger;
    private DetailPanelFigureRow figure;
    private DetailPanelEditpart editpart;
    private GroupTriangleListener groupTriangleListener;
    private NameLabelListener nameLabelListener;

    public DetailPanelEditpartRow(DetailPanelEditpart e, DetailPanelFigureRow f, DetailPanelModelRow model) {
        figure = f;
        editpart = e;
        divider = new DetailPanelDividerEditpart(editpart, figure.getDivider(), /*horizontal=*/false,
                    DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_HEIGHT, model.getRowNumber()));
        dragger = new DetailPanelDraggerEditpart(editpart, figure.getDragger(), figure.getDraggerDivider(),
                model.getRowNumber());
        groupTriangleListener = new GroupTriangleListener();
        nameLabelListener = new NameLabelListener();
        figure.getGroupTriangle().addMouseListener(groupTriangleListener);
        figure.getGroupTriangle().addMouseMotionListener(groupTriangleListener);
        figure.getNameLabel().addMouseListener(nameLabelListener);
        figure.getNameLabel().addMouseMotionListener(nameLabelListener);
    }

    public void dispose() {
        divider.dispose();
        dragger.dispose();
        figure.getGroupTriangle().removeMouseListener(groupTriangleListener);
        figure.getGroupTriangle().removeMouseMotionListener(groupTriangleListener);
        figure.getNameLabel().removeMouseListener(nameLabelListener);
        figure.getNameLabel().removeMouseMotionListener(nameLabelListener);
    }

    /* Return true if this row is a group header. */
    public boolean isGroup() {
        return figure.isGroup();
    }

    /* Return true if this row is a collapsed group header. */
    public boolean isCollapsedGroup() {
        return figure.isCollapsedGroup();
    }

    /* Return the group collapse state */
    public boolean getGroupCollapse() {
        return figure.getGroupCollapse();
    }

    /* Set the collapse state of this row */
    public void setCollapse(boolean c) {
        figure.setCollapse(c);
    }

    /* Set the shown state of this row */
    public void setShown(boolean s) {
        figure.setShown(s);
    }
}
