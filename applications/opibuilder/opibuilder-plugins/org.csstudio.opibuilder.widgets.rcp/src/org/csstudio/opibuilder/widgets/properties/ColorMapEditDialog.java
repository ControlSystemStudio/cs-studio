/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.properties;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.visualparts.HelpTrayDialog;
import org.csstudio.opibuilder.visualparts.RGBColorCellEditor;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorTuple;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;

/**The dialog to edit colormap
 * @author Xihui Chen
 *
 */
public class ColorMapEditDialog extends HelpTrayDialog {

    private Action addAction;
    private Action copyAction;
    private Action removeAction;
    private Action moveUpAction;
    private Action moveDownAction;

    private TableViewer colorListViewer;

    private List<ColorTuple> colorList;
    private PredefinedColorMap predefinedColorMap;
    private boolean autoScale;
    private boolean interpolate;

    private String title;
    private Combo preDefinedMapCombo;
    private double[] mapData;
    private Label colorMapLabel;
    private Image colorMapImage;
    private double min, max;

    public ColorMapEditDialog(Shell parentShell, ColorMap colorMap, String dialogTitle, double min, double max) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        colorList = new LinkedList<ColorTuple>();
        for(Double value : colorMap.getMap().keySet())
            colorList.add(new ColorTuple(value, colorMap.getMap().get(value)));
        autoScale = colorMap.isAutoScale();
        interpolate = colorMap.isInterpolate();
        predefinedColorMap = colorMap.getPredefinedColorMap();
        title = dialogTitle;
        this.min = min;
        this.max = max;
        mapData = new double[256];
        for(int j=0; j<256; j++)
            mapData[j] = min + j*(max-min)/255.0;
    }

    public ColorMap getOutput() {
        ColorMap result = new ColorMap();
        if(predefinedColorMap == PredefinedColorMap.None){
            LinkedHashMap<Double, RGB> map = new LinkedHashMap<Double, RGB>();
            for(ColorTuple tuple : colorList)
                map.put(tuple.value, tuple.rgb);
            result.setColorMap(map);
        }
        result.setAutoScale(autoScale);
        result.setInterpolate(interpolate);
        result.setPredefinedColorMap(predefinedColorMap);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    /**
     * Creates a label with the given text.
     *
     * @param parent
     *            The parent for the label
     * @param text
     *            The text for the label
     */
    private void createLabel(final Composite parent, final String text) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText(text);
        label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,
                false, 2, 1));
    }

    @Override
    protected String getHelpResourcePath() {
        return "/" + OPIBuilderPlugin.PLUGIN_ID + "/html/Widgets/IntensityGraph.html#colorMap"; //$NON-NLS-1$; //$NON-NLS-2$
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite parent_Composite = (Composite) super.createDialogArea(parent);

        final Composite mainComposite = new Composite(parent_Composite, SWT.None);
        mainComposite.setLayout(new GridLayout(2, false));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 300;
        mainComposite.setLayoutData(gridData);

        final Composite leftComposite = new Composite(mainComposite, SWT.None);
        leftComposite.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        leftComposite.setLayoutData(gd);
        createLabel(leftComposite, "Color Map:");

        Composite toolBarComposite = new Composite(leftComposite, SWT.BORDER);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        toolBarComposite.setLayout(gridLayout);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        toolBarComposite.setLayoutData(gd);

        ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
        GridData grid = new GridData();
        grid.horizontalAlignment = GridData.FILL;
        grid.verticalAlignment = GridData.BEGINNING;
        toolBar.setLayoutData(grid);
        createActions();
        toolbarManager.add(addAction);
        toolbarManager.add(copyAction);
        toolbarManager.add(removeAction);
        toolbarManager.add(moveUpAction);
        toolbarManager.add(moveDownAction);

        toolbarManager.update(true);

        colorListViewer = createColorListViewer(toolBarComposite);
        colorListViewer.setInput(colorList);

        Composite rightComposite = new Composite(mainComposite, SWT.NONE);
        rightComposite.setLayout(new GridLayout(1, false));
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 340;
        rightComposite.setLayoutData(gd);
        this.createLabel(rightComposite, "Use predefined color map:");

        preDefinedMapCombo = new Combo(rightComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        preDefinedMapCombo.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false,false));
        preDefinedMapCombo.setItems(PredefinedColorMap.getStringValues());


        int i=0;
        for(PredefinedColorMap colorMap : PredefinedColorMap.values()){
            if(predefinedColorMap == colorMap)
                break;
            else
                i++;
        }
        preDefinedMapCombo.select(i);



        final Button InterpolateCheckBox = new Button(rightComposite, SWT.CHECK);
        InterpolateCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,false));
        InterpolateCheckBox.setSelection(interpolate);
        InterpolateCheckBox.setText("Interpolate");

        final Button autoScaleCheckBox = new Button(rightComposite, SWT.CHECK);
        autoScaleCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,false));
        autoScaleCheckBox.setSelection(autoScale);
        autoScaleCheckBox.setText("Auto Scale");
        autoScaleCheckBox.setToolTipText("Scale the color map values to the range of" +
                " (" + min + ", " + max + ")." ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Group group = new Group(rightComposite, SWT.None);
        group.setLayoutData(new GridData(SWT.FILL, SWT.END, true, true));
        group.setLayout(new GridLayout(2, false));
        group.setText("Output" + " (" + min + "~" + max + ")" ); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-3$

        colorMapLabel = new Label(group, SWT.None);
        colorMapLabel.setLayoutData(new GridData(SWT.FILL, SWT.END, true, true));


        refreshGUI();

        preDefinedMapCombo.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                predefinedColorMap = PredefinedColorMap.values()[preDefinedMapCombo.getSelectionIndex()];
                if(preDefinedMapCombo.getSelectionIndex() != 0){
                    LinkedHashMap<Double, RGB> map =
                        PredefinedColorMap.values()[preDefinedMapCombo.getSelectionIndex()].getMap();
                    colorList.clear();
                    for(Entry<Double, RGB> entry : map.entrySet())
                        colorList.add(new ColorTuple(entry.getKey(), entry.getValue()));
                    colorListViewer.refresh();
                }
                refreshGUI();
            }
        });

        InterpolateCheckBox.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                interpolate = InterpolateCheckBox.getSelection();
                refreshGUI();
            }
        });

        autoScaleCheckBox.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                autoScale = autoScaleCheckBox.getSelection();
                refreshGUI();
            }
        });

        return parent_Composite;


    }

    /**
     * Refresh GUI when color map data changed.
     */
    private void refreshGUI() {

        Image originalImage= new Image(Display.getCurrent(),
                getOutput().drawImage(mapData, 256, 1, max, min));


        if(colorMapImage != null && !colorMapImage.isDisposed()){
            colorMapImage.dispose();
            colorMapImage = null;
        }
        colorMapImage = new Image(Display.getCurrent(), 300,40);
        GC gc = new GC(colorMapImage);
        gc.drawImage(originalImage, 0, 0, 256, 1,
                0, 0, colorMapImage.getBounds().width, colorMapImage.getBounds().height);
        colorMapLabel.setImage(colorMapImage);
        colorMapLabel.setAlignment(SWT.CENTER);
        gc.dispose();
        originalImage.dispose();
    }


    @Override
    public boolean close() {
        if(colorMapImage != null && !colorMapImage.isDisposed()){
            colorMapImage.dispose();
            colorMapImage = null;
        }
        return super.close();
    }

    /**
     * Refreshes the enabled-state of the actions.
     */
    private void refreshToolbarOnSelection() {

        IStructuredSelection selection = (IStructuredSelection) colorListViewer
                .getSelection();
        if (!selection.isEmpty()
                && selection.getFirstElement() instanceof ColorTuple) {
            removeAction.setEnabled(true);
            moveUpAction.setEnabled(true);
            moveDownAction.setEnabled(true);
            copyAction.setEnabled(true);

        } else {
            removeAction.setEnabled(false);
            moveUpAction.setEnabled(false);
            moveDownAction.setEnabled(false);
            copyAction.setEnabled(false);
        }
    }


    /**
     * @param tuple the tuple to be selected
     */
    private void refreshColorListViewerForAction(ColorTuple tuple){
        colorListViewer.refresh();
        if(tuple == null)
            colorListViewer.setSelection(StructuredSelection.EMPTY);
        else {
            colorListViewer.setSelection(new StructuredSelection(tuple));
        }
        preDefinedMapCombo.select(0);
        refreshGUI();
    }


    /**
     * Creates and configures a {@link TableViewer}.
     *
     * @param parent
     *            The parent for the table
     * @return The {@link TableViewer}
     */
    private TableViewer createColorListViewer(final Composite parent) {
        final TableViewer viewer = new TableViewer(parent, SWT.V_SCROLL
                | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        viewer.getTable().setLinesVisible(true);
        viewer.getTable().setHeaderVisible(true);

        final TableViewerColumn tvColumn = new TableViewerColumn(viewer, SWT.NONE);
        tvColumn.getColumn().setText("Value");
        tvColumn.getColumn().setMoveable(false);
        tvColumn.getColumn().setWidth(100);
        tvColumn.setEditingSupport(new ValueColumnEditingSupport(viewer, viewer.getTable()));


        tvColumn.getColumn().addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                Table table = viewer.getTable();
                int dir = table.getSortDirection();
                dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                viewer.getTable().setSortDirection(dir);
                Object[] colorTupleArray = colorList.toArray();
                Arrays.sort(colorTupleArray);
                colorList.clear();
                int i=0;
                ColorTuple[] array = new ColorTuple[colorTupleArray.length];
                for(Object o : colorTupleArray){
                    if(dir == SWT.UP)
                        array[i++] = (ColorTuple)o;
                    else
                        array[colorTupleArray.length - 1 - i++] = (ColorTuple)o;
                }
                colorList.addAll(Arrays.asList(array));
                viewer.refresh();
            }
        });


        final TableViewerColumn colorColumn = new TableViewerColumn(viewer, SWT.NONE);
        colorColumn.getColumn().setText("Color");
        colorColumn.getColumn().setMoveable(false);
        colorColumn.getColumn().setWidth(100);
        colorColumn.setEditingSupport(new ColorColumnEditingSupport(viewer, viewer.getTable()));

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new ColorListLabelProvider());
        viewer.getTable().setSortColumn(tvColumn.getColumn());
        viewer.getTable().setSortDirection(SWT.UP);

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                refreshToolbarOnSelection();
            }
        });
        viewer.getTable().setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true));
        return viewer;
    }

    /**
     * Creates the actions.
     */
    private void createActions() {
        addAction = new Action("Add") {
            @Override
            public void run() {
                ColorTuple tuple = new ColorTuple(0, new RGB(0,0,0));
                colorList.add(tuple);
                refreshColorListViewerForAction(tuple);
            }
        };
        addAction.setToolTipText("Add a color tuple");
        addAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/add.gif"));

        copyAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) colorListViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ColorTuple) {
                    ColorTuple o = (ColorTuple)selection.getFirstElement();
                    ColorTuple tuple = new ColorTuple(o.value, o.rgb);
                    colorList.add(tuple);
                    refreshColorListViewerForAction(tuple);
                }
            }
        };
        copyAction.setText("Copy");
        copyAction
                .setToolTipText("Copy the selected color tuple");
        copyAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/copy.gif"));
        copyAction.setEnabled(false);


        removeAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) colorListViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ColorTuple) {
                    colorList.remove(selection.getFirstElement());
                    refreshColorListViewerForAction(null);
                    this.setEnabled(false);
                }
            }
        };
        removeAction.setText("Remove");
        removeAction
                .setToolTipText("Remove the selected color tuple from the list");
        removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/delete.gif"));
        removeAction.setEnabled(false);

        moveUpAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) colorListViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ColorTuple) {
                    ColorTuple tuple = (ColorTuple) selection
                            .getFirstElement();
                    int i = colorList.indexOf(tuple);
                    if(i>0){
                        colorList.remove(tuple);
                        colorList.add(i-1, tuple);
                        refreshColorListViewerForAction(tuple);
                    }
                }
            }
        };
        moveUpAction.setText("Move Up");
        moveUpAction.setToolTipText("Move up the selected color tuple");
        moveUpAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/search_prev.gif"));
        moveUpAction.setEnabled(false);

        moveDownAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) colorListViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ColorTuple) {
                    ColorTuple tuple = (ColorTuple) selection
                            .getFirstElement();
                    int i = colorList.indexOf(tuple);
                    if(i<colorList.size()-1){
                        colorList.remove(tuple);
                        colorList.add(i+1, tuple);
                        refreshColorListViewerForAction(tuple);
                    }
                }
            }
        };
        moveDownAction.setText("Move Down");
        moveDownAction.setToolTipText("Move down the selected color tuple");
        moveDownAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/search_next.gif"));
        moveDownAction.setEnabled(false);

    }


    private final static class ColorListLabelProvider extends LabelProvider implements ITableLabelProvider{

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            if(columnIndex == 1 && element instanceof ColorTuple){
                return new OPIColor(((ColorTuple)element).rgb).getImage();
            }
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            if(columnIndex == 0 && element instanceof ColorTuple)
                return Double.toString(((ColorTuple)element).value);
            if(columnIndex == 1 && element instanceof ColorTuple)
                return new OPIColor(((ColorTuple)element).rgb).toString();
            return null;
        }

    }

    private final class ValueColumnEditingSupport extends EditingSupport{

        private Table table;
        public ValueColumnEditingSupport(ColumnViewer viewer, Table table) {
            super(viewer);
            this.table = table;
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return new TextCellEditor(table);
        }

        @Override
        protected Object getValue(Object element) {
            if(element instanceof ColorTuple){
                return Double.toString(((ColorTuple)element).value);
            }
            return null;
        }

        @Override
        protected void setValue(Object element, Object value) {
            if(element instanceof ColorTuple){
                String s = value == null ? "0" : value.toString(); //$NON-NLS-1$
                try {
                    ((ColorTuple)element).value = Double.parseDouble(s);
                    getViewer().refresh();
                    preDefinedMapCombo.select(0);
                    refreshGUI();
                } catch (NumberFormatException e) {
                }
            }
        }

    }


    private final class ColorColumnEditingSupport extends EditingSupport{

        private Table table;
        public ColorColumnEditingSupport(ColumnViewer viewer, Table table) {
            super(viewer);
            this.table = table;
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return new RGBColorCellEditor(table);
        }

        @Override
        protected Object getValue(Object element) {
            if(element instanceof ColorTuple){
                return ((ColorTuple)element).rgb;
            }
            return null;
        }

        @Override
        protected void setValue(Object element, Object value) {
            if(element instanceof ColorTuple){
                ((ColorTuple)element).rgb = (RGB)value;
                getViewer().refresh();
                preDefinedMapCombo.select(0);
                refreshGUI();
            }
        }

    }




}
