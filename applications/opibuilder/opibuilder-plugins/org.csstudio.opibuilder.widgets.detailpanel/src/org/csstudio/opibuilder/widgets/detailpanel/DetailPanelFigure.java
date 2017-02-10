package org.csstudio.opibuilder.widgets.detailpanel;

import java.util.LinkedList;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class DetailPanelFigure extends Figure {

    /* Data required for drawing the widget */
    private LinkedList<DetailPanelFigureRow> rows;

    /* Colours */
    private Color oddRowForegroundColor;
    private Color evenRowForegroundColor;
    private Color oddRowBackgroundColor;
    private Color evenRowBackgroundColor;
    private Color selectedBackgroundColor;
    private Color selectedForegroundColor;
    private Color borderColor;

    /* Layout information */
    private FreeformLayer pane;
    private ScrollPane scrollPane;
    private DetailPanelDividerFigure verticalDivider;
    private DetailPanelDividerFigure draggerDivider;
    private boolean editMode = false;

    /* Constructor */
    public DetailPanelFigure() {
        evenRowBackgroundColor = CustomMediaFactory.getInstance().getColor(new RGB(255,255,255));
        oddRowBackgroundColor = CustomMediaFactory.getInstance().getColor(new RGB(192,192,192));
        evenRowForegroundColor = CustomMediaFactory.getInstance().getColor(new RGB(0,0,0));
        oddRowForegroundColor = CustomMediaFactory.getInstance().getColor(new RGB(0,0,0));
        selectedForegroundColor = CustomMediaFactory.getInstance().getColor(new RGB(0,0,0));
        selectedBackgroundColor = CustomMediaFactory.getInstance().getColor(new RGB(217, 217, 255));
        borderColor = CustomMediaFactory.getInstance().getColor(new RGB(0,128,255));
        rows = new LinkedList<DetailPanelFigureRow>();
        scrollPane = new ScrollPane() {
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        pane = new FreeformLayer();
        pane.setLayoutManager(new FreeformLayout());
        add(scrollPane);
        scrollPane.setViewport(new FreeformViewport());
        scrollPane.setContents(pane);
        scrollPane.setVerticalScrollBarVisibility(ScrollPane.AUTOMATIC);
        scrollPane.setHorizontalScrollBarVisibility(ScrollPane.NEVER);
        // Add the vertical divider last so that it gets the mouse before the pane
        verticalDivider = new DetailPanelDividerFigure(pane, /*horizontal=*/true);
        pane.add(verticalDivider);
        // And a horizontal divider for use with the draggers
        draggerDivider = new DetailPanelDividerFigure(pane, /*horizontal=*/false);
        pane.add(draggerDivider);
        draggerDivider.setVisible(false);
    }

    // Return the widget for a row name.
    public Label getRowNameLabel(int rowNumber) {
        Label result = null;
        if(rowNumber >= 0 && rowNumber < rows.size()) {
            result = rows.get(rowNumber).getNameLabel();
        }
        return result;
    }

    /* Return the drawing area rectangle taking account of the scroll bar. */
    Rectangle getDrawingArea(int requiredHeight) {
        Rectangle rect = getClientArea();
        if(requiredHeight > rect.height) {
            rect.width -= scrollPane.getVerticalScrollBar().getBounds().width;
        }
        return rect;
    }

    /* Return the widget's pane. */
    Figure getPane() {
        return pane;
    }

    // Return the dragger divider figure
    DetailPanelDividerFigure getDraggerDivider() {
        return draggerDivider;
    }

    /* Add a widget created by a row to the children. */
    void addRowWidget(Figure child, boolean atEnd) {
        if(atEnd) {
            pane.add(child);
        } else {
            pane.add(child, 0);
        }
    }

    /* Swap properties between two rows */
    public void swapRowProperties(int rowA, int rowB) {
        rows.get(rowA).swapProperties(rows.get(rowB));
    }

    /* Remove a widget created by a row. */
    void removeRowWidget(Figure child) {
        pane.remove(child);
    }

    /* Return the vertical divider part of the figure */
    DetailPanelDividerFigure getVerticalDivider() {
        return verticalDivider;
    }

    /* Return a figure row */
    DetailPanelFigureRow getRow(int rowNumber) {
        return rows.get(rowNumber);
    }

    @Override
    protected void layout() {
        super.layout();
        scrollPane.setBounds(getClientArea());
        verticalDivider.layout();
        for(DetailPanelFigureRow row: rows) {
            row.layout();
        }
    }

    /* Called to do the drawing of the widget */
    @Override
    protected void paintClientArea(Graphics graphics) {
        // Draw the rows
        for(DetailPanelFigureRow row: rows) {
            row.paint(graphics);
        }
        // Super class last so that child objects are drawn on top
        super.paintClientArea(graphics);
    }

    /* Set edit mode */
    public void setEditMode(boolean m) {
        editMode = m;
        for(DetailPanelFigureRow row: rows) {
            row.setEditMode(m);
        }
    }

    /* Add a row to the figure.  */
    public void addRow() {
        DetailPanelFigureRow row = new DetailPanelFigureRow(this, rows.size());
        rows.add(row);
        row.setEditMode(editMode);
        revalidate();
        repaint();
    }

    /* Remove the last row from the figure.  */
    public void removeRow() {
        rows.getLast().dispose();
        rows.removeLast();
        revalidate();
        repaint();
    }

    /* The vertical divider position has changed */
    public void setVerticalDividerPos(int pos) {
        verticalDivider.setPosition(pos);
        invalidate();
        repaint();
    }

    /* A row divider position has changed */
    public void setRowDividerPos(int rowNumber, int pos) {
        rows.get(rowNumber).setDividerPos(pos);
        invalidate();
        repaint();
    }

    /* Even row background colour has changed */
    public void setEvenRowBackgroundColor(RGB color) {
        evenRowBackgroundColor = CustomMediaFactory.getInstance().getColor(color);
        updateRowColors();
        invalidate();
        repaint();
    }

    /* Odd row background colour has changed */
    public void setOddRowBackgroundColor(RGB color) {
        oddRowBackgroundColor = CustomMediaFactory.getInstance().getColor(color);
        updateRowColors();
        invalidate();
        repaint();
    }

    /* Even row foreground colour has changed */
    public void setEvenRowForegroundColor(RGB color) {
        evenRowForegroundColor = CustomMediaFactory.getInstance().getColor(color);
        updateRowColors();
        invalidate();
        repaint();
    }

    /* Odd row background colour has changed */
    public void setOddRowForegroundColor(RGB color) {
        oddRowForegroundColor = CustomMediaFactory.getInstance().getColor(color);
        updateRowColors();
        invalidate();
        repaint();
    }

    /* Selected foreground colour has changed */
    public void setSelectedForegroundColor(RGB color) {
        selectedForegroundColor = CustomMediaFactory.getInstance().getColor(color);
        updateRowColors();
        invalidate();
        repaint();
    }

    /* Selected background colour has changed */
    public void setSelectedBackgroundColor(RGB color) {
        selectedBackgroundColor = CustomMediaFactory.getInstance().getColor(color);
        updateRowColors();
        invalidate();
        repaint();
    }

    /* Border colour has changed */
    public void setBorderColor(RGB color) {
        borderColor = CustomMediaFactory.getInstance().getColor(color);
        updateRowColors();
        invalidate();
        repaint();
    }

    /* Return the colour appropriate for the row.*/
    public Color getRowBackgroundColor(int rowNumber, boolean selected) {
        Color result;
        if(selected && editMode) {
            result = selectedBackgroundColor;
        }
        else if((rowNumber & 1) != 0) {
            result = oddRowBackgroundColor;
        } else {
            result = evenRowBackgroundColor;
        }
        return result;
    }

    /* Return the colour appropriate for the row.*/
    public Color getRowForegroundColor(int rowNumber, boolean selected) {
        Color result;
        if(selected && editMode) {
            result = selectedForegroundColor;
        }
        else if((rowNumber & 1) != 0) {
            result = oddRowForegroundColor;
        } else {
            result = evenRowForegroundColor;
        }
        return result;
    }

    /* Return the border colour */
    public Color getBorderColor() {
        return borderColor;
    }

    /* Tell the rows a colour has changed */
    private void updateRowColors() {
        for(DetailPanelFigureRow row: rows) {
            row.setColor();
        }
    }

    /* Update the visibility of all rows */
    public void setAllRowsVisibility() {
        for(DetailPanelFigureRow row: rows) {
            row.setVisibility();
        }
    }


    /* A row has name flag has changed */
    public void setRowMode(int rowNumber, DetailPanelModelRow.Mode mode) {
        rows.get(rowNumber).setMode(mode);
        revalidate();
        repaint();
    }

    /* A row level has changed */
    public void setRowLevel(int rowNumber, DetailPanelModel.DisplayLevel level) {
        rows.get(rowNumber).setLevel(level);
        revalidate();
        repaint();
    }

    /* Get a row level */
    public DetailPanelModel.DisplayLevel getRowLevel(int rowNumber) {
        return rows.get(rowNumber).getLevel();
    }

    /* A row has group collapse state has changed */
    public void setGroupCollapse(int rowNumber, boolean collapse) {
        rows.get(rowNumber).setGroupCollapse(collapse);
        revalidate();
        repaint();
    }

    /* A row name has changed */
    public void setRowName(int rowNumber, String name) {
        rows.get(rowNumber).setName(name);
        revalidate();
        repaint();
    }

    /* A row name value has changed */
    public void setRowNameValue(int rowNumber, String name) {
        rows.get(rowNumber).setNameValue(name);
        revalidate();
        repaint();
    }

    /* A row tooltip has changed */
    public void setRowTooltip(int rowNumber, String tip) {
        rows.get(rowNumber).setTooltip(tip);
        revalidate();
        repaint();
    }

    /* A row name drawing area has changed.  Note that this gets
     * called during the layout of the model so should not do
     * any invalidating of the drawing. */
    public void setRowNameArea(int rowNumber, Rectangle area) {
        rows.get(rowNumber).setNameArea(area);
    }

    /* A visible row number has changed.  Note that this gets
     * called during the layout of the model so should not do
     * any invalidating of the drawing. */
    public void setVisibleRowNumber(int rowNumber, int visibleRowNumber) {
        rows.get(rowNumber).setVisibleRowNumber(visibleRowNumber);
    }

    /* Return the number of rows created so far */
    public int getRowCount() {
        return rows.size();
    }

    /* Clean up any image resources */
    public void dispose() {
        // Currently I don't have any resources loaded
    }

    /* Return the content pane. */
    public IFigure getContentPane() {
        return pane;
    }

    /* Return the index of the row immediately after the boundary that is
     * nearest to the given Y coordinate.  If the top of the panel is
     * nearest, 0 is returned.  If the bottom of the panel is nearest,
     * the number of rows is returned. A row that is not visible is not considered.*/
    public int findNearestRowBoundary(int y) {
        int nearestBottomRow = 0;
        int nearestBottom = -1;
        int nearestTopRow = 0;
        int nearestTop = -1;
        int result = 0;
        int visibleRows = 0;
        for(DetailPanelFigureRow row: rows) {
            if(row.isVisible()) {
                visibleRows++;
                if(row.distanceToTop(y) < nearestTop || nearestTop < 0) {
                    nearestTop = row.distanceToTop(y);
                    nearestTopRow = row.getRowNumber();
                }
                if(row.distanceToBottom(y) < nearestBottom || nearestBottom < 0) {
                    nearestBottom = row.distanceToBottom(y);
                    nearestBottomRow = row.getRowNumber();
                }
            }
        }
        if(nearestBottom < nearestTop) {
            result = nearestBottomRow + 1;
        } else {
            result = nearestTopRow;
        }
        // Are we after the last visible row?
        if(result == visibleRows) {
            // Yes, so we should return after the last row.
            result = rows.size();
        }
        return result;
    }

    /* Return true if the next row is a group member. */
    public boolean isNextRowGroupMember(int rowNumber) {
        boolean result = false;
        if((rowNumber + 1) < rows.size()) {
            result = rows.get(rowNumber+1).isGroupMember();
        }
        return result;
    }

    /* Deselect all rows */
    public void deselectAll() {
        for(DetailPanelFigureRow row: rows) {
            row.deselect();
        }
    }

    /* Is a row selected? */
    public boolean isRowSelected(int rowNumber) {
        boolean result = false;
        if(rowNumber < rows.size()) {
            result = rows.get(rowNumber).isSelected();
        }
        return result;
    }
}
