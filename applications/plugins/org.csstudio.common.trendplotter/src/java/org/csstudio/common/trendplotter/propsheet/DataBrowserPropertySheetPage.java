/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.propsheet;

import java.util.ArrayList;
import java.util.Arrays;

import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.ArchiveDataSource;
import org.csstudio.common.trendplotter.model.ArchiveRescale;
import org.csstudio.common.trendplotter.model.AxisConfig;
import org.csstudio.common.trendplotter.model.FormulaItem;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.ModelItem;
import org.csstudio.common.trendplotter.model.ModelListener;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.ui.AddPVAction;
import org.csstudio.common.trendplotter.ui.StartEndTimeAction;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/** Sheet for Eclipse Properties View that displays Data Browser Model,
 *  i.e. the Configuration Panel for the Data Browser.
 *  <p>
 *  <b>Note about life cycle, at least under Eclipse 3.5.1:</b>
 *  When a Properties view is open and a DataBrowserEditor is selected,
 *  a corresponding DataBrowserPropertySheetPage is created.
 *
 *  It stays around even after the Properties is closed or changed
 *  to another editor, presumably to allow RCP to quickly re-display
 *  the properties when the editor is again selected.
 *
 *  The DataBrowserPropertySheetPage is only disposed after the associated
 *  DataBrowserEditor closes.
 *
 *  Under some circumstances, the DataBrowserPropertySheetPage is even
 *  created for a closed-then-re-opened DataBrowserEditor while there is
 *  no visible Properties view.
 *
 *  In summary, the 'Model' for this DataBrowserPropertySheetPage never changes.
 *  Each DataBrowserEditor.getAdapter will create a new DataBrowserPropertySheetPage.
 *
 *  @author Kay Kasemir
 */
public class DataBrowserPropertySheetPage extends Page implements IPropertySheetPage, ModelListener {
    /** Model to display/edit in property sheet */
    private final Model model;

    /** Undo/redo operations manager */
    private final OperationsManager operations_manager;

    /** Top-level control for the property sheet */
    private Composite control;

    private Text formula_txt;

    private TableViewer trace_table;

    private TableViewer archive_table;

    private Composite archive_panel;

    private Composite formula_panel;

    private Text start_time;

    private Text end_time;

    private Button rescales[];

    private ColorBlob background;

    private Text update_period;

    /** Initialize
     *  @param model Model to display/edit
     */
    public DataBrowserPropertySheetPage(final Model model,
                                        final OperationsManager operations_manager) {
        this.model = model;
        this.operations_manager = operations_manager;
    }

    /** {@inheritDoc} */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        // NOP. Seems not to get called
    }

    /** {@inheritDoc} */
    @Override
    public Control getControl() {
        return control;
    }

    /** {@inheritDoc} */
    @Override
    public void createControl(final Composite parent) {
        control = new Composite(parent, 0);
        control.setLayout(new FillLayout());

        // Tabs: Traces, Time Axis, ...
        final TabFolder tab_folder = new TabFolder(control, SWT.TOP);
        createTracesTab(tab_folder);
        createTimeAxisTab(tab_folder);
        createValueAxesTab(tab_folder);
        createMiscTab(tab_folder);

        model.addListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose() {
        model.removeListener(this);
        super.dispose();
    }

    /** Create tab for traces (PVs, Formulas)
     *  @param tabs
     */
    private void createTracesTab(final TabFolder tabs) {
        // TabItem with SashForm
        final TabItem tab = new TabItem(tabs, 0);
        tab.setText(Messages.TracesTab);

        final SashForm sashform = new SashForm(tabs, SWT.VERTICAL | SWT.BORDER);
        sashform.setLayout(new FillLayout());
        tab.setControl(sashform);

        createTracesTabItemPanel(sashform);
        createTracesMenuAndToolbarActions();

        createTracesTabDetailPanel(sashform);
        createArchiveMenu();

//        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
//        manager.add(new BulkEditAction());
//        manager.add(new Separator());
//        // Other plug-ins can contribute there actions here
//        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//
//        Menu menu = manager.createContextMenu(trace_table.getControl());
//        trace_table.getControl().setMenu(menu);
////        getSite().registerContextMenu(menu.manager, trace_table);
        
        trace_table.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                updateTracesTabDetailPanel();
            }
        });

        sashform.setWeights(new int[] { 60, 40 });
    }

    /** Within SashForm of the "Traces" tab, create the Model Item table
     *  @param sashform
     */
    private void createTracesTabItemPanel(final SashForm sashform) {
        // TableColumnLayout requires the TableViewer to be in its own Composite!
        final Composite model_item_top = new Composite(sashform, SWT.BORDER);
        final TableColumnLayout table_layout = new TableColumnLayout();
        model_item_top.setLayout(table_layout);
        trace_table = new TableViewer(model_item_top, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.VIRTUAL);
        final Table table = trace_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        final TraceTableHandler tth = new TraceTableHandler();
        tth.createColumns(table_layout, operations_manager, trace_table);

        trace_table.setContentProvider(tth);
        trace_table.setInput(model);
        
        new ControlSystemDragSource(trace_table.getControl())
        {
            @Override
            public Object getSelection()
            {
                final IStructuredSelection selection = (IStructuredSelection) trace_table.getSelection();
                final Object[] objs = selection.toArray();
                final ModelItem[] items = Arrays.copyOf(objs, objs.length, ModelItem[].class);
                return items;
            }
        };
    }

    /** Within SashForm of the "Traces" tab, create the Item item detail panel:
     *  PV archive data sources, Formula
     *  @param sashform
     *  @param trace_table TableViewer for the trace table
     */
    private void createTracesTabDetailPanel(final SashForm sashform) {
        final Composite item_detail_top = new Composite(sashform, SWT.BORDER);
        item_detail_top.setLayout(new FormLayout());

        archive_panel = new Composite(item_detail_top, 0);
        FormData fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        archive_panel.setLayoutData(fd);
        archive_panel.setLayout(new GridLayout());
        Label l = new Label(archive_panel, 0);
        l.setText(Messages.ArchiveDataSources);
        l.setLayoutData(new GridData());

        // TableColumnLayout requires the TableViewer to be in its own Composite!
        final Composite table_parent = new Composite(archive_panel, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);
        archive_table = new TableViewer(table_parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.VIRTUAL);
        final Table table = archive_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        final ArchivesTableHandler ath = new ArchivesTableHandler();
        ath.createColumns(table_layout, operations_manager, archive_table);
        archive_table.setContentProvider(ath);

        formula_panel = new Composite(item_detail_top, 0);
        fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        formula_panel.setLayoutData(fd);
        formula_panel.setLayout(new GridLayout(2, false));

        l = new Label(formula_panel, 0);
        l.setText(Messages.FormulaLabel);
        l.setLayoutData(new GridData());

        formula_txt = new Text(formula_panel, SWT.READ_ONLY | SWT.BORDER);
        formula_txt.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        formula_txt.setToolTipText(Messages.FormulaLabelEditTT);
        formula_txt.addMouseListener(new MouseListener() {
            @Override
            public void mouseDown(final MouseEvent e) {
                final Object item = ((IStructuredSelection) trace_table.getSelection())
                        .getFirstElement();
                if (!(item instanceof FormulaItem)) {
                    return;
                }
                final FormulaItem formula = (FormulaItem) item;
                final EditFormulaDialog dlg = new EditFormulaDialog(operations_manager, formula_txt
                        .getShell(), formula);
                dlg.open();
            }

            @Override
            public void mouseUp(final MouseEvent e) { /* NOP */
            }

            @Override
            public void mouseDoubleClick(final MouseEvent e) { /* NOP */
            }
        });

        archive_panel.setVisible(false);
        formula_panel.setVisible(false);
    }

    /** Update the lower 'detail' section of the "Traces" tab
     *  based on the currently selected item
     */
    private void updateTracesTabDetailPanel() {
        // Show PV or Formula Panel depending on selection
        final IStructuredSelection sel = (IStructuredSelection) trace_table.getSelection();
        if (sel.size() == 1) {
            final Object item = sel.getFirstElement();
            if (item instanceof PVItem) {
                formula_panel.setVisible(false);
                archive_panel.setVisible(true);
                archive_table.setInput(item);
                return;
            }
            if (item instanceof FormulaItem) {
                final FormulaItem formula = (FormulaItem) item;
                formula_txt.setText(formula.getExpression());
                archive_table.setInput(null);
                archive_panel.setVisible(false);
                formula_panel.setVisible(true);
                return;
            }
        }
        // else: Neither PV nor formula, or multiple items selected
        archive_table.setInput(null);
        archive_panel.setVisible(false);
        formula_panel.setVisible(false);
    }

    /** Create context menu and toolbar actions for the traces table */
    private void createTracesMenuAndToolbarActions() {
        final MenuManager menu = new MenuManager();
        menu.setRemoveAllWhenShown(true);
        final Shell shell = trace_table.getControl().getShell();
        final AddPVAction add_pv = new AddPVAction(operations_manager, shell, model, false);
        final AddPVAction add_formula = new AddPVAction(operations_manager, shell, model, true);
        final DeleteItemsAction delete_pv = new DeleteItemsAction(operations_manager,
                                                                  trace_table,
                                                                  model);
        menu.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                menu.add(add_pv);
                menu.add(add_formula);
                menu.add(delete_pv);
                final PVItem pvs[] = getSelectedPVs();
                if (pvs.length > 1) {
                    menu.add(new BulkEditTraceAction(shell, pvs, operations_manager, model));
                }
                menu.add(new RemoveUnusedAxesAction(operations_manager, model));
                if (pvs.length <= 0) {
                    return;
                }
                menu.add(new AddArchiveAction(operations_manager, shell, pvs));
                menu.add(new UseDefaultArchivesAction(operations_manager, pvs));
            }
        });

        final Table table = trace_table.getTable();
        table.setMenu(menu.createContextMenu(table));
        // Allow object contributions based on selected items
        getSite().registerContextMenu(menu.getId(), menu, trace_table);

        // Add to tool bar
        final IToolBarManager toolbar = getSite().getActionBars().getToolBarManager();
        toolbar.add(add_pv);
        toolbar.add(add_formula);
    }

    /** Create context menu for the archive table,
     *  which depends on the currently selected PVs
     */
    private void createArchiveMenu() {
        // Create dynamic context menu, content changes depending on selections
        final MenuManager menu = new MenuManager();
        menu.setRemoveAllWhenShown(true);
        menu.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                // Determine selected PV Items
                final PVItem pvs[] = getSelectedPVs();
                if (pvs.length <= 0) {
                    return;
                }

                menu.add(new AddArchiveAction(operations_manager, control.getShell(), pvs));
                menu.add(new UseDefaultArchivesAction(operations_manager, pvs));

                // Only allow removal of archives from single PV
                if (pvs.length != 1) {
                    return;
                }

                // Determine selected archives
                final IStructuredSelection arch_sel = (IStructuredSelection) archive_table
                        .getSelection();
                if (arch_sel.isEmpty()) {
                    return;
                }
                final Object[] objects = arch_sel.toArray();
                final ArchiveDataSource archives[] = new ArchiveDataSource[objects.length];
                for (int i = 0; i < archives.length; i++) {
                    archives[i] = (ArchiveDataSource) objects[i];
                }
                menu.add(new DeleteArchiveAction(operations_manager, pvs[0], archives));
            }
        });
        final Table table = archive_table.getTable();
        table.setMenu(menu.createContextMenu(table));
    }

    /** @return Currently selected PVs in the table of traces (items). Never <code>null</code> */
    protected PVItem[] getSelectedPVs() {
        final IStructuredSelection selection = (IStructuredSelection) trace_table.getSelection();
        if (selection.isEmpty()) {
            return new PVItem[0];
        }
        final Object obj[] = selection.toArray();
        final ArrayList<PVItem> pvs = new ArrayList<PVItem>();
        for (int i = 0; i < obj.length; ++i) {
            if (obj[i] instanceof PVItem) {
                pvs.add((PVItem) obj[i]);
            }
        }
        return pvs.toArray(new PVItem[pvs.size()]);
    }

    /** Create tab for traces (PVs, Formulas)
     *  @param tabs
     */
    private void createTimeAxisTab(final TabFolder tab_folder) {
        final TabItem time_tab = new TabItem(tab_folder, 0);
        time_tab.setText(Messages.TimeAxis);

        final Composite parent = new Composite(tab_folder, 0);
        parent.setLayout(new GridLayout(3, false));

        // Start Time: ______ [...]
        Label label = new Label(parent, 0);
        label.setText(Messages.StartTimeLbl);
        label.setLayoutData(new GridData());

        start_time = new Text(parent, SWT.BORDER);
        start_time.setToolTipText(Messages.StartTimeTT);
        start_time.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        final Button start_end = new Button(parent, SWT.PUSH);
        start_end.setText(Messages.StartEndDialogBtn);
        start_end.setToolTipText(Messages.StartEndDialogTT);
        start_end.setLayoutData(new GridData());

        // End Time:   ______ [...]
        label = new Label(parent, 0);
        label.setText(Messages.EndTimeLbl);
        label.setLayoutData(new GridData());

        end_time = new Text(parent, SWT.BORDER);
        end_time.setToolTipText(Messages.EndTimeTT);
        end_time.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        final Button start_end2 = new Button(parent, SWT.PUSH);
        start_end2.setText(Messages.StartEndDialogBtn);
        start_end2.setToolTipText(Messages.StartEndDialogTT);
        start_end2.setLayoutData(new GridData());

        time_tab.setControl(parent);

        // Initialize with model's current start/end time
        changedTimerange();

        // Allow entry of start/end times in text boxes
        final SelectionAdapter times_entered = new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                try {
                    StartEndTimeAction.run(model,
                                           operations_manager,
                                           start_time.getText(),
                                           end_time.getText());
                } catch (final Exception ex) {
                    MessageDialog.openError(parent.getShell(),
                                            Messages.Error,
                                            Messages.InvalidStartEndTimeError);
                    // Restore unchanged model time range
                    changedTimerange();
                }
            }
        };
        start_time.addSelectionListener(times_entered);
        end_time.addSelectionListener(times_entered);

        // Buttons start start/end dialog
        final SelectionListener start_end_action = new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                StartEndTimeAction.run(parent.getShell(), model, operations_manager);
            }
        };
        start_end.addSelectionListener(start_end_action);
        start_end2.addSelectionListener(start_end_action);
    }

    /** Create tab for traces (PVs, Formulas)
     *  @param tabs
     */
    private void createValueAxesTab(final TabFolder tab_folder) {
        final TabItem axes_tab = new TabItem(tab_folder, 0);
        axes_tab.setText(Messages.ValueAxes);

        final Composite parent = new Composite(tab_folder, 0);
        parent.setLayout(new GridLayout());

        // Rescale options
        final Composite rescale = new Composite(parent, 0);
        rescale.setLayout(new RowLayout());
        rescale.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        final Label l = new Label(rescale, 0);
        l.setText(Messages.ArchiveRescale_Label);
        final ArchiveRescale rescale_items[] = ArchiveRescale.values();
        rescales = new Button[rescale_items.length];
        for (int i = 0; i < rescales.length; ++i) {
            final ArchiveRescale item = rescale_items[i];
            if (item.ordinal() != i)
             {
                throw new Error("ArchiveRescale items out of order"); //$NON-NLS-1$
            }

            rescales[i] = new Button(rescale, SWT.RADIO);
            rescales[i].setText(item.toString());
            if (model.getArchiveRescale() == item) {
                rescales[i].setSelection(true);
            }
            rescales[i].addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    // Called for both the newly selected radio button
                    // and the one that's not de-selected.
                    // Only react to the selected button
                    if (model.getArchiveRescale() == item) {
                        return;
                    }
                    new ChangeArchiveRescaleCommand(model, operations_manager, item);
                }
            });
        }

        // TableColumnLayout requires the TableViewer to be in its own Composite!
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);
        final AxesTableHandler ath = new AxesTableHandler(table_parent,
                                                          table_layout,
                                                          operations_manager);
        ath.getAxesTable().setInput(model);

        axes_tab.setControl(parent);
    }

    /** Create tab for misc. config items (update period, colors)
     *  @param tabs
     */
    private void createMiscTab(final TabFolder tab_folder) {
        final TabItem misc_tab = new TabItem(tab_folder, 0);
        misc_tab.setText("Misc."); //$NON-NLS-1$

        final Composite parent = new Composite(tab_folder, 0);
        parent.setLayout(new GridLayout(2, false));

        // Redraw period: ______
        Label label = new Label(parent, 0);
        label.setText(Messages.UpdatePeriodLbl);
        label.setLayoutData(new GridData());

        update_period = new Text(parent, SWT.BORDER);
        update_period.setText(Double.toString(model.getUpdatePeriod()));
        update_period.setToolTipText(Messages.UpdatePeriodTT);
        update_period.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        update_period.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                try {
                    final double period = Double.parseDouble(update_period.getText().trim());
                    new ChangeUpdatePeriodCommand(model, operations_manager, period);
                } catch (final Exception ex) {
                    update_period.setText(Double.toString(model.getUpdatePeriod()));
                }
            }
        });

        // Background Color: ______
        label = new Label(parent, 0);
        label.setText(Messages.BackgroundColorLbl);
        label.setLayoutData(new GridData());

        background = new ColorBlob(parent, model.getPlotBackground());
        background.setToolTipText(Messages.BackgroundColorTT);
        final GridData gd = new GridData();
        gd.minimumWidth = 80;
        gd.widthHint = 80;
        gd.heightHint = 15;
        background.setLayoutData(gd);
        background.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final ColorDialog dialog = new ColorDialog(parent.getShell());
                dialog.setRGB(model.getPlotBackground());
                final RGB value = dialog.open();
                if (value != null) {
                    new ChangePlotBackgroundCommand(model, operations_manager, value);
                }
            }
        });

        misc_tab.setControl(parent);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus() {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public void changedUpdatePeriod() {
        update_period.setText(Double.toString(model.getUpdatePeriod()));
    }

    /** {@inheritDoc} */
    @Override
    public void changedArchiveRescale() {
        final int selected = model.getArchiveRescale().ordinal();
        for (int i = 0; i < rescales.length; ++i) {
            final boolean desired = i == selected;
            if (rescales[i].getSelection() != desired) {
                rescales[i].setSelection(desired);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void changedColors() {
        background.setColor(model.getPlotBackground());
    }

    /** Update the start/end time in the Time axis panel when model changes
     *  {@inheritDoc}
     */
    @Override
    public void changedTimerange() {
        start_time.setText(model.getStartSpecification());
        end_time.setText(model.getEndSpecification());
    }

    /** {@inheritDoc} */
    @Override
    public void changedAxis(final AxisConfig axis) {
        // Axes Table handles this
    }

    /** {@inheritDoc} */
    @Override
    public void itemAdded(final ModelItem item) {
        // Trace Table handles it
    }

    /** {@inheritDoc} */
    @Override
    public void itemRemoved(final ModelItem item) {
        // Trace Table handles it
    }

    /** {@inheritDoc} */
    @Override
    public void changedItemVisibility(final ModelItem item) {
        // Trace Table handles it
    }

    /** {@inheritDoc} */
    @Override
    public void changedItemLook(final ModelItem item) {
        updateTracesTabDetailPanel();
    }

    /** {@inheritDoc} */
    @Override
    public void changedItemDataConfig(final PVItem item) {
        updateTracesTabDetailPanel();
    }

    /** {@inheritDoc} */
    @Override
    public void scrollEnabled(final boolean scroll_enabled) {
        changedTimerange();
    }

    @Override
    public void changedAnnotations() {
        // TODO Auto-generated method stub

    }

    @Override
    public void changedXYGraphConfig() {
        // TODO Auto-generated method stub

    }
}
