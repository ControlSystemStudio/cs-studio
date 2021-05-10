package org.csstudio.opibuilder.widgets.detailpanel;

import java.util.LinkedList;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.PVNameProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

public class DetailPanelModel extends AbstractContainerModel implements IPVWidgetModel {

    // The display level enumeration
    public enum DisplayLevel {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");
        private String description;
        private DisplayLevel(String description) {
            this.description = description;
        }
        public static String[] stringValues() {
            String[] sv = new String[values().length];
            int i = 0;
            for (DisplayLevel p : values())
                sv[i++] = p.toString();
            return sv;
        }
        @Override
        public String toString() {
            return description;
        }
    }

    /* Constants */
    public static final int MAX_ROW_COUNT = 1000;

    /* The widget properties */
    public static final String PROP_ROW_COUNT = "row_count";
    public static final String PROP_VERT_DIVIDER_POS = "vert_divider_pos";
    public static final String PROP_COLOR_ODD_ROW_BACK = "color_odd_row_back";
    public static final String PROP_COLOR_EVEN_ROW_BACK = "color_even_row_back";
    public static final String PROP_COLOR_ODD_ROW_FORE = "color_odd_row_fore";
    public static final String PROP_COLOR_EVEN_ROW_FORE = "color_even_row_fore";
    public static final String PROP_COLOR_SELECT_FORE = "color_select_fore";
    public static final String PROP_COLOR_SELECT_BACK = "color_select_back";
    public static final String PROP_DISPLAY_LEVEL = "display_level";

    /* The rows */
    private LinkedList<DetailPanelModelRow> rows;

    /* Id of this widget model */
    public static final String ID = "org.csstudio.opibuilder.widgets.detailpanel";

    /* Constructor */
    public DetailPanelModel() {
        // Initialise the rows
        rows = new LinkedList<DetailPanelModelRow>();
        // What does this do? How can the model have a size?
        setSize(300, 200);
    }

    /* Create the properties */
    @Override
    protected void configureProperties() {
        // Widget properties
        addPVProperty(new PVNameProperty(PROP_PVNAME, "PV Name", WidgetPropertyCategory.Basic,
                ""), new PVValueProperty(PROP_PVVALUE, null));
        addProperty(new IntegerProperty(PROP_ROW_COUNT, "Row Count",
                WidgetPropertyCategory.Behavior, /*default=*/1, /*min=*/1, /*max=*/200000));
        addProperty(new IntegerProperty(PROP_VERT_DIVIDER_POS, "Vertical Divider",
                WidgetPropertyCategory.Display, /*default=*/20, /*min=*/0, /*max=*/50000));
        addProperty(new ColorProperty(PROP_COLOR_ODD_ROW_BACK, "Odd Row Background Color",
                WidgetPropertyCategory.Display, new RGB(192, 192, 192)));
        addProperty(new ColorProperty(PROP_COLOR_EVEN_ROW_BACK, "Even Row Background Color",
                WidgetPropertyCategory.Display, new RGB(255, 255, 255)));
        addProperty(new ColorProperty(PROP_COLOR_ODD_ROW_FORE, "Odd Row Foreground Color",
                WidgetPropertyCategory.Display, new RGB(0, 0, 0)));
        addProperty(new ColorProperty(PROP_COLOR_EVEN_ROW_FORE, "Even Row Foreground Color",
                WidgetPropertyCategory.Display, new RGB(0, 0, 0)));
        addProperty(new ColorProperty(PROP_COLOR_SELECT_FORE, "Selected Row Foreground Color",
                WidgetPropertyCategory.Display, new RGB(0, 0, 0)));
        addProperty(new ColorProperty(PROP_COLOR_SELECT_BACK, "Selected Row Background Color",
                WidgetPropertyCategory.Display, new RGB(217, 217, 255)));
        addProperty(new ComboProperty(PROP_DISPLAY_LEVEL, "Display Level",
                WidgetPropertyCategory.Display, DetailPanelModel.DisplayLevel.stringValues(), 0));
        removeProperty(PROP_COLOR_BACKGROUND);
        removeProperty(PROP_COLOR_FOREGROUND);

        // Row properties
        for(int i=0; i<MAX_ROW_COUNT; i++) {
            DetailPanelModelRow.declareProperties(this, i);
        }
    }

    /* Shift row properties up one to make space for a new row */
    public void rightShiftRowProperties(int rowNumber) {
        for(int j = getChildren().size()-1; j>rowNumber; j--){
            rows.get(j).copyProperties(rows.get(j-1));
        }
    }

    /* Shift row properties down one to remove them for a deleted row */
    public void leftShiftRowProperties(int rowNumber) {
        for(int j=rowNumber; j<getChildren().size(); j++) {
            rows.get(j).copyProperties(rows.get(j+1));
        }
    }

    /* Swap properties between two rows */
    public void swapRowProperties(int rowA, int rowB) {
        rows.get(rowA).swapProperties(rows.get(rowB));
    }

    /* Return our widget identifier */
    @Override
    public String getTypeID() {
        return ID;
    }

    /* Return the number of rows parameter */
    public int getRowCount() {
        return (Integer)getPropertyValue(PROP_ROW_COUNT);
    }

    /* Return the vertical divider position */
    public int getVerticalDividerPos() {
        return (Integer)getPropertyValue(PROP_VERT_DIVIDER_POS);
    }

    /* Get a row property value.  The base property name is
     * extended by the row number to access a row specific property. */
    public Object getRowPropertyValue(int rowNumber, String propertyName) {
        return rows.get(rowNumber).getPropertyValue(propertyName);
    }

    /* Set the number of rows in the model. */
    public void setRows(int numberOfRows) {
        while(rows.size() > numberOfRows) {
            rows.getLast().dispose();
            rows.removeLast();
        }
        while(rows.size() < numberOfRows) {
            rows.add(new DetailPanelModelRow(this, rows.size()));
        }
        setPropertyValue(getProperty(PROP_ROW_COUNT).getPropertyID(), rows.size());
    }

    /* Get a specific row */
    public DetailPanelModelRow getRow(int rowNumber) {
        DetailPanelModelRow result = null;
        if(rowNumber >= 0 && rowNumber < rows.size()) {
            result = rows.get(rowNumber);
        }
        return result;
    }

    /* Determine the layout of the rows within the given rectangle.*/
    /* Returns the height required by the layout.  */
    public int adjustLayout(Rectangle rect) {
        int yStart = 0;
        // Where's the vertical divider?
        int verticalDividerPos = (int)getPropertyValue(PROP_VERT_DIVIDER_POS);
        // Something temporary for now
        if(rows.size() > 0) {
            int height = rect.height() / rows.size();
            int visibleRowNumber = 0;
            for(DetailPanelModelRow row: rows) {
                yStart = row.adjustLayout(rect.width, yStart, height, verticalDividerPos);
                visibleRowNumber = row.setVisibleRowNumber(visibleRowNumber);
            }
        }
        return yStart;
    }

    /* Return the appropriate background colour for a row */
    public RGB getRowBackgroundColor(int rowNumber) {
        RGB result;
        if((rowNumber & 1) != 0) {
            result = getRGBFromColorProperty(PROP_COLOR_ODD_ROW_BACK);
        } else {
            result = getRGBFromColorProperty(PROP_COLOR_EVEN_ROW_BACK);
        }
        return result;
    }

    /* Return the odd row background colour */
    public RGB getOddRowBackgroundColor() {
        return getRGBFromColorProperty(PROP_COLOR_ODD_ROW_BACK);
    }

    /* Return the even row background colour */
    public RGB getEvenRowBackgroundColor() {
        return getRGBFromColorProperty(PROP_COLOR_EVEN_ROW_BACK);
    }

    /* Return the odd row foreground colour */
    public RGB getOddRowForegroundColor() {
        return getRGBFromColorProperty(PROP_COLOR_ODD_ROW_FORE);
    }

    /* Return the even row foreground colour */
    public RGB getEvenRowForegroundColor() {
        return getRGBFromColorProperty(PROP_COLOR_EVEN_ROW_FORE);
    }

    /* One or more of the colour properties have changed */
    public void colorsChanged() {
        // Tell the rows to update their colours
        for(DetailPanelModelRow row: rows) {
            row.colorsChanged();
        }
    }

    /* Return the rows of the model */
    public LinkedList<DetailPanelModelRow> getRows() {
        return rows;
    }

    /* Set the collapse state of a group's member row */
    public void setCollapse(int rowNumber, boolean collapse) {
        DetailPanelModelRow row = rows.get(rowNumber);
        row.setCollapse(collapse);
    }

    /* Set the shown state of a group's member row */
    public void setShown(int rowNumber, boolean shown) {
        DetailPanelModelRow row = rows.get(rowNumber);
        row.setShown(shown);
    }

    /* Return the display level parameter */
    public DisplayLevel getDisplayLevel() {
        return DetailPanelModel.DisplayLevel.values()[(int)getPropertyValue(PROP_DISPLAY_LEVEL)];
    }

    /* Returning false for this prevents arbitrary children
     * being placed under the detail panel model object.  It probably
     * does other things too.*/
    @Override
    public boolean isChildrenOperationAllowable() {
        return false;
    }


    /* These functions provide the IPVWidgetModel interface */

    @Override
    public boolean isBorderAlarmSensitve(){
        return false;
    }

    @Override
    public boolean isForeColorAlarmSensitve(){
        return false;
    }

    @Override
    public boolean isBackColorAlarmSensitve(){
        return false;
    }

    @Override
    public boolean isAlarmPulsing(){
        return false;
    }

    @Override
    public String getPVName(){
        return null;
    }

}

