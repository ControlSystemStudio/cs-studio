package org.csstudio.opibuilder.widgets.detailpanel;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.PVNameProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.draw2d.geometry.Rectangle;

public class DetailPanelModelRow {

    // The row mode enumeration
    public enum Mode {
        INDENTED("Indented"),
        FULLWIDTH("Full Width"),
        STARTEXPANDED("Start Expanded"),
        STARTCOLLAPSED("Start Collapsed");
        private String description;
        private Mode(String description) {
            this.description = description;
        }
        public static String[] stringValues() {
            String[] sv = new String[values().length];
            int i = 0;
            for (Mode p : values())
                sv[i++] = p.toString();
            return sv;
        }
        @Override
        public String toString() {
            return description;
        }
    }

    /* The row properties */
    public static final String PROP_ROW_MODE = "row_mode";
    public static final String PROP_ROW_NAME = "row_name";
    public static final String PROP_ROW_NAME_VALUE = "row_name_value";
    public static final String PROP_ROW_HEIGHT = "row_height";
    public static final String PROP_ROW_TOOLTIP = "row_tooltip";
    public static final String PROP_ROW_LEVEL = "row_level";

    /* Row information */
    private int rowNumber;
    private int visibleRowNumber = 0;
    private DetailPanelModel model;
    private GroupingContainerModel children;
    private Rectangle nameArea;
    private boolean collapsed = false;
    private boolean shown = true;
    private boolean childrenVisible  = false;

    /* Make a row property name */
    public static String makePropertyName(String propertyName, int rowNumber) {
        return "row_" + rowNumber + "_" + propertyName;
    }

    /* Make a row category */
    private static WidgetPropertyCategory makePropertyCategory(final int rowNumber) {
        // A row category appears to be just a string but it has been wrapped up inside
        // an instance of the WidgetPropertyCategory a little unnecessarily in my view.
        // You could argue it makes things type safe, but there is really no safety
        // beyond what a regular String would provide.
        WidgetPropertyCategory category = new WidgetPropertyCategory() {
            @Override
            public String toString() {
                return "Row " + String.format("%03d", rowNumber);
            }
        };
        return category;
    }

    /* Declare the row properties */
    public static void declareProperties(DetailPanelModel model, int rowNumber) {
        WidgetPropertyCategory category = makePropertyCategory(rowNumber);
        String propName;
        String propNameValue;
        propName = makePropertyName(PROP_ROW_MODE, rowNumber);
        model.addProperty(new ComboProperty(propName,    "Mode", category, Mode.stringValues(), Mode.STARTEXPANDED.ordinal()));
        model.setPropertyVisible(propName, false);

        propName = makePropertyName(PROP_ROW_NAME, rowNumber);
        propNameValue = makePropertyName(PROP_ROW_NAME_VALUE, rowNumber);
        model.addPVProperty(new PVNameProperty(propName, "Name", category, ""),
                new PVValueProperty(propNameValue, null));
        model.setPropertyVisible(propName, false);

        propName = makePropertyName(PROP_ROW_HEIGHT, rowNumber);
        model.addProperty(new IntegerProperty(propName,    "Height", category,
                /*default=*/20, /*min=*/0, /*max=*/50000));
        model.setPropertyVisible(propName, false);
        propName = makePropertyName(PROP_ROW_TOOLTIP, rowNumber);
        model.addProperty(new StringProperty(propName,    "Tooltip", category, "", /*multiline=*/true));
        model.setPropertyVisible(propName, false);
        propName = makePropertyName(PROP_ROW_LEVEL, rowNumber);
        model.addProperty(new ComboProperty(propName,    "Min Display Level", category,
                DetailPanelModel.DisplayLevel.stringValues(), 0));
        model.setPropertyVisible(propName, false);
    }

    /* Constructor */
    public DetailPanelModelRow(DetailPanelModel model, int rowNumber) {
        this.rowNumber = rowNumber;
        this.model = model;
        this.nameArea = new Rectangle();
        // Make the property page rows visible
        // Following properties only exist for rows less than the max_row_count
        if (rowNumber < DetailPanelModel.MAX_ROW_COUNT) {
            this.model.setPropertyVisible(makePropertyName(PROP_ROW_MODE, this.rowNumber), true);
            this.model.setPropertyVisible(makePropertyName(PROP_ROW_NAME, this.rowNumber), true);
            this.model.setPropertyVisible(makePropertyName(PROP_ROW_HEIGHT, this.rowNumber), true);
            this.model.setPropertyVisible(makePropertyName(PROP_ROW_TOOLTIP, this.rowNumber), true);
            this.model.setPropertyVisible(makePropertyName(PROP_ROW_LEVEL, this.rowNumber), true);
        }
        // Does the children container already exist?
        // When the model is created from a stored file, the container is created for us.
        List<AbstractWidgetModel> modelChildren = model.getChildren();
        if(modelChildren.size() > rowNumber) {
            // Use the container created for us.  Luckily they will exist as children in row number order.
            this.children = (GroupingContainerModel)modelChildren.get(rowNumber);
        } else {
            // Make the container to contain the children
            this.children = new GroupingContainerModel();
            // Add child needs to be performed in the UI thread.
            UIBundlingThread.getInstance().addRunnable(new Runnable() {
                @Override
                public void run() {
                    DetailPanelModelRow.this.model.addChild(DetailPanelModelRow.this.children);
                }
            });
        }
        childrenVisible = true;
        // Initialise the container
        this.children.setName("Row");
        this.children.setLocation(1,1); // Don't know what this does?
        this.children.setBackgroundColor(model.getRowBackgroundColor(visibleRowNumber));
        this.children.setBorderStyle(BorderStyle.NONE);
        this.children.setPropertyValue(GroupingContainerModel.PROP_TRANSPARENT, false);
        this.children.setPropertyValue(AbstractWidgetModel.PROP_VISIBLE, true);
        this.children.setScaleOptions(false, false, true);
        this.children.setPropertyValue(GroupingContainerModel.PROP_SHOW_SCROLLBAR, false);
    }

    /* Return the row number */
    public int getRowNumber() {
        return rowNumber;
    }

    /* One or more colour property has changed */
    public void colorsChanged() {
        this.children.setBackgroundColor(model.getRowBackgroundColor(visibleRowNumber));
    }

    /* Clean up */
    public void dispose() {
        // Hide the property page rows
        // Following properties only exist for rows less than the max_row_count
        if (rowNumber < DetailPanelModel.MAX_ROW_COUNT) {
            model.setPropertyVisible(makePropertyName(PROP_ROW_MODE, rowNumber), false);
            model.setPropertyVisible(makePropertyName(PROP_ROW_NAME, rowNumber), false);
            model.setPropertyVisible(makePropertyName(PROP_ROW_HEIGHT, rowNumber), false);
            model.setPropertyVisible(makePropertyName(PROP_ROW_TOOLTIP, rowNumber), false);
            model.setPropertyVisible(makePropertyName(PROP_ROW_LEVEL, rowNumber), false);
        }
        // Disconnect the container, needs to happen in the UI thread.
        if(!collapsed) {
            UIBundlingThread.getInstance().addRunnable(new Runnable() {
                @Override
                public void run() {
                    model.removeChild(children);
                    children = null;
                }
            });
        }
    }

    /* Copy the properties from the other row into this one */
    public void copyProperties(DetailPanelModelRow other) {
        model.setPropertyValue(makePropertyName(PROP_ROW_MODE, rowNumber),
                other.model.getPropertyValue(makePropertyName(PROP_ROW_MODE, other.rowNumber)));
        model.setPropertyValue(makePropertyName(PROP_ROW_NAME, rowNumber),
                other.model.getPropertyValue(makePropertyName(PROP_ROW_NAME, other.rowNumber)));
        model.setPropertyValue(makePropertyName(PROP_ROW_HEIGHT, rowNumber),
                other.model.getPropertyValue(makePropertyName(PROP_ROW_HEIGHT, other.rowNumber)));
        model.setPropertyValue(makePropertyName(PROP_ROW_TOOLTIP, rowNumber),
                other.model.getPropertyValue(makePropertyName(PROP_ROW_TOOLTIP, other.rowNumber)));
        model.setPropertyValue(makePropertyName(PROP_ROW_LEVEL, rowNumber),
                other.model.getPropertyValue(makePropertyName(PROP_ROW_LEVEL, other.rowNumber)));
    }

    protected void swapProperty(DetailPanelModelRow other, String property, boolean fire) {
        Object a, b;
        a = other.model.getPropertyValue(makePropertyName(property, other.rowNumber));
        b = model.getPropertyValue(makePropertyName(property, rowNumber));
        model.setPropertyValue(makePropertyName(property, rowNumber), a, fire);
        other.model.setPropertyValue(makePropertyName(property, other.rowNumber), b, fire);
    }

    /* Swap the properties between the other row and this one */
    public void swapProperties(final DetailPanelModelRow other) {
        swapProperty(other, PROP_ROW_MODE, false);
        swapProperty(other, PROP_ROW_NAME, true);
        swapProperty(other, PROP_ROW_HEIGHT, true);
        swapProperty(other, PROP_ROW_TOOLTIP, true);
        swapProperty(other, PROP_ROW_LEVEL, true);

        boolean t;
        t = other.collapsed;
        other.collapsed = collapsed;
        collapsed = t;
        t = other.shown;
        other.shown = shown;
        shown = t;
        t = other.childrenVisible;
        other.childrenVisible = childrenVisible;
        childrenVisible = t;
        Rectangle tr = other.nameArea;
        other.nameArea = nameArea;
        nameArea = tr;

        List<AbstractWidgetModel> otherChildren = new LinkedList<AbstractWidgetModel>(other.children.getChildren());
        List<AbstractWidgetModel> myChildren = new LinkedList<AbstractWidgetModel>(children.getChildren());
        other.children.removeAllChildren();
        children.removeAllChildren();
        for(AbstractWidgetModel child: otherChildren) {
            children.addChild(child, true);
        }
        for(AbstractWidgetModel child: myChildren) {
            other.children.addChild(child, true);
        }


//        UIBundlingThread.getInstance().addRunnable(new Runnable() {
//            @Override
//            public void run() {
//                model.removeChild(children);
//                other.model.removeChild(other.children);
//            }
//        });
//        GroupingContainerModel t = other.children;
//        other.children = children;
//        children = t;
//        UIBundlingThread.getInstance().addRunnable(new Runnable() {
//            @Override
//            public void run() {
//                model.addChild(children);
//                other.model.addChild(other.children);
//            }
//        });
    }

    /* Get a row property value.  Check to see if this function is ever called. */
    public Object getPropertyValue(String propertyName) {
        return model.getPropertyValue(makePropertyName(propertyName, rowNumber));
    }

    /* Return the row mode. */
    public Mode getMode() {
        return Mode.values()[(int)model.getPropertyValue(makePropertyName(PROP_ROW_MODE, rowNumber))];
    }

    /* Set the row mode. */
    public void setMode(DetailPanelModelRow.Mode mode, boolean fire) {
        model.setPropertyValue(makePropertyName(PROP_ROW_MODE, rowNumber), mode.ordinal(), fire);
    }

    /* Return the row level. */
    public DetailPanelModel.DisplayLevel getLevel() {
        return DetailPanelModel.DisplayLevel.values()[(int)model.getPropertyValue(makePropertyName(PROP_ROW_LEVEL, rowNumber))];
    }

    /* Return the row name. */
    public String getName() {
        return (String)model.getPropertyValue(makePropertyName(PROP_ROW_NAME, rowNumber));
    }

    /* Return the row minimum height. */
    public int getHeight() {
        return (int)model.getPropertyValue(makePropertyName(PROP_ROW_HEIGHT, rowNumber));
    }

    /* Return the tooltip. */
    public String getTooltip() {
        return (String)model.getPropertyValue(makePropertyName(PROP_ROW_TOOLTIP, rowNumber));
    }

    /* Adjust the layout of the container and the name area.
     * Returns the start y of the next row. */
    public int adjustLayout(int width, int y, int height, int verticalDividerPos) {
        // What height do the children take up?
        int childrenHeight = getHeight();
        for(AbstractWidgetModel child: children.getChildren()) {
            Rectangle childRect = child.getBounds();
            if((childRect.y + childRect.height) > childrenHeight) {
                childrenHeight = childRect.y + childRect.height;
            }
        }
        // Adjust the row height to match
        if(childrenHeight > getHeight()) {
            model.setPropertyValue(makePropertyName(PROP_ROW_HEIGHT, rowNumber), childrenHeight);
        }
        // Zero height if children not visible
        if(!childrenVisible) {
            childrenHeight = 0;
        }
        // Set the bounds
        if(getMode() == Mode.FULLWIDTH) {
            children.setBounds(0, y, width, childrenHeight);
            nameArea.setBounds(0, y, 0, childrenHeight);
        } else {
            children.setBounds(verticalDividerPos, y, width-verticalDividerPos, childrenHeight);
            nameArea.setBounds(0, y, verticalDividerPos, childrenHeight);
        }
        return y + childrenHeight;
    }

    /* Set the visible row number and return the next visible row number. */
    public int setVisibleRowNumber(int n) {
        visibleRowNumber = n;
        if(!collapsed) {
            n++;
        }
        children.setBackgroundColor(model.getRowBackgroundColor(visibleRowNumber));
        return n;
    }

    /* Return the visible row number. */
    public int getVisibleRowNumber() {
        return visibleRowNumber;
    }

    /* Return the previously calculated name area for this row */
    public Rectangle getNameArea() {
        return nameArea;
    }

    /* Set the collapse state of this group member row. */
    public void setCollapse(boolean c) {
        collapsed = c;
        controlVisibility();
    }

    /* Set the shown state of the row */
    public void setShown(boolean s) {
        shown = s;
        controlVisibility();
    }

    /* Control the visibility of the child container. */
    /* JAT: I don't know what I was thinking here.  The children object
     * cannot be removed from the model as this causes problems when
     * writing out to the file while a group is collapsed.  The reason
     * for removing the children from the model eludes me at the moment
     * although the drag and drop across a group boundary does not appear
     * to work properly now.
     */
    public void controlVisibility() {
        boolean newVisible = !collapsed && shown;
        if(newVisible && !childrenVisible) {
            // Put the container back in the tree
            //UIBundlingThread.getInstance().addRunnable(new Runnable() {
            //    @Override
            //    public void run() {
            //        model.addChild(children);
            //    }
            //});
            childrenVisible = true;
        } else if(!newVisible && childrenVisible) {
            // Remove the container from the tree
            //UIBundlingThread.getInstance().addRunnable(new Runnable() {
            //    @Override
            //    public void run() {
            //        model.removeChild(children);
            //    }
            //});
            childrenVisible = false;
        }
    }

}

