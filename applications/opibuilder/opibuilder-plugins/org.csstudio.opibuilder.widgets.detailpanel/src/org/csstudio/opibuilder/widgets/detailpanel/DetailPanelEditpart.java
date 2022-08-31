package org.csstudio.opibuilder.widgets.detailpanel;

import java.util.LinkedList;
import java.util.logging.Level;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.dnd.DropPVtoPVWidgetEditPolicy;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.editparts.ConnectionHandler;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.IPVWidgetEditpart;
import org.csstudio.opibuilder.editparts.PVWidgetConnectionHandler;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.VTypeHelper;
import org.diirt.vtype.VType;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.RGB;

public class DetailPanelEditpart extends AbstractContainerEditpart implements IPVWidgetEditpart {

    // The PV widget delegate
    protected DetailPanelPvEditpartDelegate delegate;

    // There should really be a row edit part.
    private LinkedList<DetailPanelEditpartRow> rows;

    /* Constructor */
    public DetailPanelEditpart() {
        delegate = new DetailPanelPvEditpartDelegate(this);
        rows = new LinkedList<DetailPanelEditpartRow>();
    }

    /* Create and initialise the figure*/
    @Override
    protected IFigure doCreateFigure() {
        DetailPanelFigure theFigure = new DetailPanelFigure();
        new DetailPanelDividerEditpart(this, theFigure.getVerticalDivider(),
                /*horizontal=*/true, DetailPanelModel.PROP_VERT_DIVIDER_POS);
        return theFigure;
    }

    /* Get the right kind of model object */
    @Override
    public DetailPanelModel getWidgetModel() {
        return (DetailPanelModel)super.getWidgetModel();
    }

    /* Get the right kind of figure object */
    @Override
    public DetailPanelFigure getFigure() {
        return (DetailPanelFigure)super.getFigure();
    }

    /* A class that allows row property change handlers to be registered. */
    class RowPropertyChangeHandler implements IWidgetPropertyChangeHandler {
        protected int rowNumber;
        public RowPropertyChangeHandler(int rowNumber) {
            this.rowNumber = rowNumber;
        }
        public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
            return true;
        }
    }

    /* Property change handlers receive the notifications of property changes. */
    @Override
    protected void registerPropertyChangeHandlers() {
        // The main properties
        setPropertyChangeHandler(DetailPanelModel.PROP_ROW_COUNT, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setRows((Integer)newValue);
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_VERT_DIVIDER_POS, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setVerticalDividerPos((int)newValue);
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_EVEN_ROW_BACK, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setEvenRowBackgroundColor(((OPIColor)newValue).getRGBValue());
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_ODD_ROW_BACK, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setOddRowBackgroundColor(((OPIColor)newValue).getRGBValue());
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_EVEN_ROW_FORE, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setEvenRowForegroundColor(((OPIColor)newValue).getRGBValue());
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_ODD_ROW_FORE, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setOddRowForegroundColor(((OPIColor)newValue).getRGBValue());
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_SELECT_FORE, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setSelectedForegroundColor(((OPIColor)newValue).getRGBValue());
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_SELECT_BACK, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setSelectedBackgroundColor(((OPIColor)newValue).getRGBValue());
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_BORDER_COLOR, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setBorderColor(((OPIColor)newValue).getRGBValue());
                }
                return false;
            }
        });
        setPropertyChangeHandler(DetailPanelModel.PROP_DISPLAY_LEVEL, new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if(newValue != null) {
                    DetailPanelEditpart.this.setShown(DetailPanelModel.DisplayLevel.values()[(int)newValue]);
                }
                return false;
            }
        });
        // The row properties
        for(int i=0; i<DetailPanelModel.MAX_ROW_COUNT; i++) {
            setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_MODE, i), new RowPropertyChangeHandler(i) {
                public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                    if(newValue != null) {
                        DetailPanelEditpart.this.setRowMode(rowNumber, DetailPanelModelRow.Mode.values()[(int)newValue]);
                    }
                    return false;
                }
            });
            setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_NAME, i), new RowPropertyChangeHandler(i) {
                public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                    if(newValue != null) {
                        DetailPanelEditpart.this.setRowName(rowNumber, (String)newValue);
                    }
                    return false;
                }
            });
            setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_NAME_VALUE, i), new RowPropertyChangeHandler(i) {
                public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                    // A pv aware widget uses a derivative of the secret VType class
                    if(newValue != null) {
                        if(newValue instanceof VType) {
                            // The secret VTypeHelper can be used to access the VType instance
                            DetailPanelEditpart.this.setRowNameValue(rowNumber, VTypeHelper.getString((VType)newValue));
                        }
                    }
                    return false;
                }
            });
            setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_HEIGHT, i), new RowPropertyChangeHandler(i) {
                public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                    if(newValue != null) {
                        DetailPanelEditpart.this.setRowDividerPos(rowNumber, (int)newValue);
                    }
                    return false;
                }
            });
            setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_TOOLTIP, i), new RowPropertyChangeHandler(i) {
                public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                    if(newValue != null) {
                        DetailPanelEditpart.this.setRowTooltip(rowNumber, (String)newValue);
                    }
                    return false;
                }
            });
            setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_LEVEL, i), new RowPropertyChangeHandler(i) {
                public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                    if(newValue != null) {
                        DetailPanelEditpart.this.setRowLevel(rowNumber, DetailPanelModel.DisplayLevel.values()[(int)newValue]);
                    }
                    return false;
                }
            });
        }
    }

    /* The widget is being activated in the editor.  This function
     * is called whenever a new figure is created.  We need to register
     * any figure listeners and initialise the figure from the model data. */
    private int requiredHeight = 0;
    @Override
    public void activate() {
        // Base class
        super.activate();
        // The PV delegate
        delegate.startPVs();
        // Initialise the figure from the model
        updateFigures(true);
        // Register a layout listener
        getFigure().addLayoutListener(new LayoutListener.Stub() {
            @Override
            public void postLayout(IFigure Container) {
                // Layout the model.  In the layout, when the vertical scroll bar is displayed we want
                // to reduce the width of the available display by the width of the scroll bar so that
                // we don't need a horizontal scroll bar.  To do this, we use the height arrived at by
                // the previous layout to predict whether the scroll bar will appear or not.  The success
                // of this relies on the fact that there are quite a few screen redraws whenever the
                // layout changes.
                requiredHeight = getWidgetModel().adjustLayout(getFigure().getDrawingArea(requiredHeight));
                // Tell the figure
                for(DetailPanelModelRow row: getWidgetModel().getRows()) {
                    getFigure().setRowNameArea(row.getRowNumber(), row.getNameArea());
                    getFigure().setVisibleRowNumber(row.getRowNumber(), row.getVisibleRowNumber());
                }
            }
        });
    }

    /* Update the figure from the model */
    public void updateFigures(boolean initialState) {
        // Update the figure from the model
        setRows(getWidgetModel().getRowCount());
        getFigure().setOddRowBackgroundColor(getWidgetModel().getOddRowBackgroundColor());
        getFigure().setEvenRowBackgroundColor(getWidgetModel().getEvenRowBackgroundColor());
        getFigure().setOddRowForegroundColor(getWidgetModel().getOddRowForegroundColor());
        getFigure().setEvenRowForegroundColor(getWidgetModel().getEvenRowForegroundColor());
        getFigure().setVerticalDividerPos(getWidgetModel().getVerticalDividerPos());
        for(DetailPanelModelRow row: getWidgetModel().getRows()) {
            // Following properties only exist for rows less than the max_row_count
            if (row.getRowNumber() < DetailPanelModel.MAX_ROW_COUNT) {
                getFigure().setRowMode(row.getRowNumber(), row.getMode());
                getFigure().setRowName(row.getRowNumber(), row.getName());
                getFigure().setRowDividerPos(row.getRowNumber(), row.getHeight());
                getFigure().setRowLevel(row.getRowNumber(), row.getLevel());
            }
        }
        // Set visibility
        getFigure().setEditMode(getExecutionMode() == ExecutionMode.EDIT_MODE);
        setAllGroupCollapse(initialState);
        setShown(getWidgetModel().getDisplayLevel());
    }

    /* A row mode property has been changed */
    public synchronized void setRowMode(int rowNumber, DetailPanelModelRow.Mode mode) {
        getFigure().setRowMode(rowNumber, mode);
        setAllGroupCollapse(false);
    }

    /* A row level property has been changed */
    public synchronized void setRowLevel(int rowNumber, DetailPanelModel.DisplayLevel level) {
        getFigure().setRowLevel(rowNumber, level);
    }

    /* A row name property has been changed */
    public synchronized void setRowName(int rowNumber, String name) {
        getFigure().setRowName(rowNumber, name);
    }

    /* A row name property has been changed */
    public synchronized void setRowNameValue(int rowNumber, String name) {
        getFigure().setRowNameValue(rowNumber, name);
    }

    /* A row tooltip property has been changed */
    public synchronized void setRowTooltip(int rowNumber, String tip) {
        getFigure().setRowTooltip(rowNumber, tip);
    }

    /* The number of rows property has been changed */
    public synchronized void setRows(int numberOfRows) {
        getWidgetModel().setRows(numberOfRows);
        while(rows.size() > numberOfRows) {
            getFigure().removeRow();
            rows.getLast().dispose();
            rows.removeLast();
        }
        while(rows.size() < numberOfRows && rows.size() < DetailPanelModel.MAX_ROW_COUNT) {
            DetailPanelModelRow row = getWidgetModel().getRow(rows.size());
            getFigure().addRow();
            rows.add(new DetailPanelEditpartRow(this, getFigure().getRow(rows.size()), row));
            getFigure().setRowMode(row.getRowNumber(), row.getMode());
            getFigure().setRowName(row.getRowNumber(), row.getName());
            getFigure().setRowDividerPos(row.getRowNumber(), row.getHeight());
        }
        if (numberOfRows > DetailPanelModel.MAX_ROW_COUNT) {
            String errorMessage = "Detail panel widget: number of rows requested (" + numberOfRows + ") is greater "
                    + "than the limit of " + DetailPanelModel.MAX_ROW_COUNT;
            ConsoleService.getInstance().writeError(errorMessage);
            OPIBuilderPlugin.getLogger().log(Level.WARNING, errorMessage);
        }
        getWidgetModel().setRows(rows.size());
        setAllGroupCollapse(false);
    }

    /* The vertical divider position has changed */
    public synchronized void setVerticalDividerPos(int pos) {
        getFigure().setVerticalDividerPos(pos);
    }

    /* A row divider position has changed */
    public synchronized void setRowDividerPos(int rowNumber, int pos) {
        getFigure().setRowDividerPos(rowNumber, pos);
    }

    /* The odd row background colour has changed. */
    public synchronized void setOddRowBackgroundColor(RGB color) {
        getWidgetModel().colorsChanged();
        getFigure().setOddRowBackgroundColor(color);
    }

    /* The even row background colour has changed. */
    public synchronized void setEvenRowBackgroundColor(RGB color) {
        getWidgetModel().colorsChanged();
        getFigure().setEvenRowBackgroundColor(color);
    }

    /* The odd row foreground colour has changed. */
    public synchronized void setOddRowForegroundColor(RGB color) {
        getFigure().setOddRowForegroundColor(color);
    }

    /* The even row foreground colour has changed. */
    public synchronized void setEvenRowForegroundColor(RGB color) {
        getFigure().setEvenRowForegroundColor(color);
    }

    /* The selected foreground colour has changed. */
    public synchronized void setSelectedForegroundColor(RGB color) {
        getFigure().setSelectedForegroundColor(color);
    }

    /* The selected background colour has changed. */
    public synchronized void setSelectedBackgroundColor(RGB color) {
        getFigure().setSelectedBackgroundColor(color);
    }

    /* The border colour has changed. */
    public synchronized void setBorderColor(RGB color) {
        getFigure().setBorderColor(color);
    }

    /* Not sure what this does */
    @Override
    protected final EditPart createChild(final Object model) {
        EditPart result = super.createChild(model);
        if (result instanceof AbstractBaseEditPart) {
            ((AbstractBaseEditPart) result).setSelectable(false);
        }
        return result;
    }

    /* Installs the edit features required by the detail panel */
    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();
        installEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE,
                new DropPVtoPVWidgetEditPolicy());
        installEditPolicy(EditPolicy.CONTAINER_ROLE, null);
        installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
        installEditPolicy("DETAILPANEL", new DetailPanelEditPolicy(this));
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DetailPanelEditPolicyRow());
    }

    @Override
    public void performRequest(Request request){
        int rowNumber = getFirstSelectedRow();
        if (rowNumber >= 0 &&
                getWidgetModel().getRow(rowNumber).getMode() != DetailPanelModelRow.Mode.FULLWIDTH &&
                getExecutionMode() == ExecutionMode.EDIT_MODE && (
                request.getType() == RequestConstants.REQ_DIRECT_EDIT ||
                request.getType() == RequestConstants.REQ_OPEN)) {
            new DetailPanelRowEditManager(this, rowNumber,
                    new DetailPanelRowCellEditorLocator(getFigure().getRowNameLabel(rowNumber))).show();
        }
    }

    /* Clean up the figure. */
    @Override
    public void deactivate() {
        getFigure().dispose();
        super.deactivate();
    }

    @Override
    public IFigure getContentPane() {
        return getFigure().getContentPane();
    }

    /* Set the collapse state of all the row.  Set the initialState flag
     * to true when the widget is being activated the first time. */
    public void setAllGroupCollapse(boolean initialState) {
        boolean collapse = false;
        for(int rowIndex=0; rowIndex<rows.size(); rowIndex++) {
            DetailPanelEditpartRow row = rows.get(rowIndex);
            if(row.isGroup()) {
                collapse = row.getGroupCollapse();
                if(initialState && row.isCollapsedGroup()) {
                    getFigure().setGroupCollapse(rowIndex, true);
                    collapse = true;
                }
            } else {
                row.setCollapse(collapse);
                getWidgetModel().setCollapse(rowIndex, collapse);
            }
        }
    }

    /* Set the collapse state of the group and change the display state
     * of the group's rows */
    public void setGroupCollapse(int groupRowNumber, boolean collapse) {
        // Change the group collapse state
        getFigure().setGroupCollapse(groupRowNumber, collapse);
        // Show or hide the group's rows
        int rowIndex = groupRowNumber;
        boolean going = true;
        while(going) {
            rowIndex++;
            if(rowIndex >= rows.size()) {
                going = false;
            } else {
                DetailPanelEditpartRow row = rows.get(rowIndex);
                if(row.isGroup()) {
                    going = false;
                } else {
                    row.setCollapse(collapse);
                    getWidgetModel().setCollapse(rowIndex, collapse);
                }
            }
        }
    }

    /* Set the shown state of the rows. */
    public void setShown(DetailPanelModel.DisplayLevel displayLevel) {
        DetailPanelModel.DisplayLevel groupLevel = DetailPanelModel.DisplayLevel.LOW;
        // For each row...
        for(int rowIndex=0; rowIndex<rows.size(); rowIndex++) {
            DetailPanelEditpartRow row = rows.get(rowIndex);
            // Get the level of this row
            DetailPanelModel.DisplayLevel rowLevel = getFigure().getRowLevel(rowIndex);
            // Record the level of the prevailing group
            if(row.isGroup()) {
                groupLevel = rowLevel;
            }
            // The level of this row cannot be less than its containing group
            if(rowLevel.ordinal() < groupLevel.ordinal()) {
                rowLevel = groupLevel;
            }
            // Show the row if its level is less than or equal to the display level
            boolean showRow = rowLevel.ordinal() <= displayLevel.ordinal();
            row.setShown(showRow);
            getWidgetModel().setShown(rowIndex, showRow);
        }
    }

    /* Move a row.*/
    public void moveRow(int oldIndex, int newIndex, DetailPanelModelRow.Mode mode) {
        // Set the new mode
        getWidgetModel().getRow(oldIndex).setMode(mode, false);
        getFigure().getRow(oldIndex).setMode(mode);
        // Swap the two rows
        if(oldIndex == newIndex) {
            // Row not moving
        }
        if(oldIndex > newIndex) {
            for(int i=newIndex; i<oldIndex; i++) {
                getWidgetModel().swapRowProperties(oldIndex,  i);
                getFigure().swapRowProperties(oldIndex, i);
            }
        } else if(oldIndex < newIndex) {
            for(int i=newIndex-1; i>oldIndex; i--) {
                getWidgetModel().swapRowProperties(oldIndex,  i);
                getFigure().swapRowProperties(oldIndex, i);
            }
        }
    }

    /* Work out the set of changes needed to move a row from one location to another.
     * The changes are added to the command object as items that are implemented sequentially.
     */
    public void determineRowMove(int oldIndex, int newIndex, DetailPanelChangeRowIndexCommand cmd) {
        // Get some rows
        DetailPanelModelRow sourceRow = getWidgetModel().getRow(oldIndex);
        DetailPanelModelRow rowAfter = getWidgetModel().getRow(newIndex);
        DetailPanelModelRow rowBefore = getWidgetModel().getRow(newIndex-1);
        // What we do depends on the row mode
        if(sourceRow.getMode() == DetailPanelModelRow.Mode.FULLWIDTH) {
            // If the row is full width, the row after the new position cannot be indented
            if(rowAfter != null && rowAfter.getMode() == DetailPanelModelRow.Mode.INDENTED) {
                cmd.addItem(newIndex, newIndex, DetailPanelModelRow.Mode.INDENTED, DetailPanelModelRow.Mode.STARTEXPANDED);
            }
            // But the row itself moves unchanged
            cmd.addItem(oldIndex, newIndex, DetailPanelModelRow.Mode.FULLWIDTH, DetailPanelModelRow.Mode.FULLWIDTH);
        } else if(sourceRow.getMode() == DetailPanelModelRow.Mode.INDENTED) {
            // If the row is indented, it changes to start expanded if the row above it is absent or full width
            DetailPanelModelRow.Mode newMode = DetailPanelModelRow.Mode.INDENTED;
            if(rowBefore == null || rowBefore.getMode() == DetailPanelModelRow.Mode.FULLWIDTH) {
                newMode = DetailPanelModelRow.Mode.STARTEXPANDED;
            }
            cmd.addItem(oldIndex, newIndex, DetailPanelModelRow.Mode.INDENTED, newMode);
        } else {
            if(newIndex < oldIndex) {
                // The group is moving up
                DetailPanelModelRow.Mode newMode = sourceRow.getMode();
                if(rowAfter != null && rowAfter.getMode() == DetailPanelModelRow.Mode.INDENTED) {
                    newMode = DetailPanelModelRow.Mode.INDENTED;
                }
                cmd.addItem(oldIndex, newIndex, sourceRow.getMode(), newMode);
                // Move all the following indented rows up too
                int childOffset = 1;
                DetailPanelModelRow childRow = getWidgetModel().getRow(oldIndex+childOffset);
                while(childRow != null && childRow.getMode() == DetailPanelModelRow.Mode.INDENTED) {
                    cmd.addItem(oldIndex+childOffset, newIndex+childOffset, DetailPanelModelRow.Mode.INDENTED, DetailPanelModelRow.Mode.INDENTED);
                    childOffset++;
                    childRow = getWidgetModel().getRow(oldIndex+childOffset);
                }
            } else {
                // The group is moving down, find the index of the row after the end of the group
                int indexAfterGroup = oldIndex+1;
                DetailPanelModelRow rowAfterGroup = getWidgetModel().getRow(indexAfterGroup);
                while(rowAfterGroup != null && rowAfterGroup.getMode() == DetailPanelModelRow.Mode.INDENTED) {
                    indexAfterGroup++;
                    rowAfterGroup = getWidgetModel().getRow(indexAfterGroup);
                }
                if(newIndex > indexAfterGroup) {
                    // The group is being moved somewhere after the end of the group
                    DetailPanelModelRow.Mode newMode = sourceRow.getMode();
                    if(rowAfter != null && rowAfter.getMode() == DetailPanelModelRow.Mode.INDENTED) {
                        newMode = DetailPanelModelRow.Mode.INDENTED;
                    }
                    cmd.addItem(oldIndex, newIndex, sourceRow.getMode(), newMode);
                    // Move all the following indented rows down too
                    int childOffset = 1;
                    DetailPanelModelRow childRow = getWidgetModel().getRow(oldIndex+childOffset);
                    while(childRow != null && childRow.getMode() == DetailPanelModelRow.Mode.INDENTED) {
                        cmd.addItem(oldIndex, newIndex, DetailPanelModelRow.Mode.INDENTED, DetailPanelModelRow.Mode.INDENTED);
                        childOffset++;
                        childRow = getWidgetModel().getRow(oldIndex+childOffset);
                    }
                } else {
                    // The group is being moved to within the group, make the first child the
                    // new group head and the old head just a child
                    cmd.addItem(oldIndex+1, oldIndex+1, DetailPanelModelRow.Mode.INDENTED, DetailPanelModelRow.Mode.STARTEXPANDED);
                    cmd.addItem(oldIndex, newIndex, sourceRow.getMode(), DetailPanelModelRow.Mode.INDENTED);
                }
            }
        }
    }

    /* Deselect all rows within the figure */
    public void deselectAll() {
        getFigure().deselectAll();
    }

    /* Create commands that indent all the selected rows */
    public CompoundCommand indentSelectedRows() {
        // Somewhere to accumulate the commands
        CompoundCommand cmds = new CompoundCommand();
        // For each row...
        for(int rowIndex=0; rowIndex<rows.size(); rowIndex++) {
            // Can the row be indented?
            if(rowIndex > 0 && getFigure().isRowSelected(rowIndex) &&
                    getWidgetModel().getRow(rowIndex).getMode() != DetailPanelModelRow.Mode.FULLWIDTH &&
                    getWidgetModel().getRow(rowIndex).getMode() != DetailPanelModelRow.Mode.INDENTED) {
                cmds.add(new SetWidgetPropertyCommand(getWidgetModel(),
                        DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_MODE, rowIndex),
                        DetailPanelModelRow.Mode.INDENTED.ordinal()));
            }
        }
        return cmds;
    }

    /* Create commands that outdent all the selected rows */
    public CompoundCommand outdentSelectedRows() {
        // Somewhere to accumulate the commands
        CompoundCommand cmds = new CompoundCommand();
        // For each row...
        for(int rowIndex=0; rowIndex<rows.size(); rowIndex++) {
            // Can the row be indented?
            if(getFigure().isRowSelected(rowIndex) &&
                    getWidgetModel().getRow(rowIndex).getMode() == DetailPanelModelRow.Mode.INDENTED) {
                cmds.add(new SetWidgetPropertyCommand(getWidgetModel(),
                        DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_MODE, rowIndex),
                        DetailPanelModelRow.Mode.STARTEXPANDED.ordinal()));
            }
        }
        return cmds;
    }

    /* Return true if a row selection can be outdented */
    public boolean canOutdent() {
        boolean result = true;
        // For each row...
        for(int rowIndex=0; rowIndex<rows.size() && !result; rowIndex++) {
            // Can the row be indented?
            if(getFigure().isRowSelected(rowIndex) &&
                    getWidgetModel().getRow(rowIndex).getMode() == DetailPanelModelRow.Mode.INDENTED) {
                result = true;
            }
        }
        return result;
    }

    /* Return the index of the first selected row, -1 for none */
    public int getFirstSelectedRow() {
        int result = -1;
        for(int rowIndex=0; rowIndex<rows.size() && result < 0; rowIndex++) {
            // Is the row selected?
            if(getFigure().isRowSelected(rowIndex)) {
                result = rowIndex;
            }
        }
        return result;
    }

    /* Overrides required by the PV delegate */
    @Override
    protected void doActivate() {
        super.doActivate();
        delegate.doActivate();
    }

    @Override
    protected ConnectionHandler createConnectionHandler() {
        return new PVWidgetConnectionHandler(this);
    }

    @Override
    protected void doDeActivate() {
        if(isActive()){
            delegate.doDeActivate();
            super.doDeActivate();
        }
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
        if(key == ProcessVariable.class){
            return new ProcessVariable(getPVName());
        }
        return super.getAdapter(key);
    }

    public void setIgnoreOldPVValue(boolean ignoreOldValue) {
        delegate.setIgnoreOldPVValue(ignoreOldValue);
    }

    @Override
    protected void registerBasePropertyChangeHandlers() {
        super.registerBasePropertyChangeHandlers();
        delegate.registerBasePropertyChangeHandlers();
    }

    protected void markAsControlPV(String pvPropId, String pvValuePropId){
        delegate.markAsControlPV(pvPropId, pvValuePropId);
    }

    @Override
    protected void initFigure(IFigure figure) {
        super.initFigure(figure);
        delegate.initFigure(figure);
    }

    /* These functions implement the IPVWidgetEditpart interface */

    @Override
    public String[] getAllPVNames() {
        return delegate.getAllPVNames();
    }

    @Override
    public IPV getControlPV() {
        return delegate.getControlPV();
    }

    @Override
    public IPV getPV() {
        return delegate.getPV();
    }

    @Override
    public String getPVName() {
        return delegate.getPVName();
    }

    @Override
    public IPV getPV(String pvPropId) {
        return delegate.getPV(pvPropId);
    }

    @Override
    public VType getPVValue(String pvPropId) {
        return delegate.getPVValue(pvPropId);
    }

    @Override
    public void setPVValue(String pvPropId, Object value) {
        delegate.setPVValue(pvPropId, value);
    }

    @Override
    public void addSetPVValueListener(ISetPVValueListener listener) {
        delegate.addSetPVValueListener(listener);
    }

    @Override
    public boolean isPVControlWidget() {
        return delegate.isPVControlWidget();
    }

    @Override
    public void setControlEnabled(boolean enabled) {
        delegate.setControlEnabled(enabled);
    }

}
