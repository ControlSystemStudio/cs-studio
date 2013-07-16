/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.propsheet;

import java.util.ArrayList;
import java.util.Arrays;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.AxisConfig;
import org.csstudio.trends.sscan.model.FormulaItem;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.csstudio.trends.sscan.model.ModelListener;
import org.csstudio.trends.sscan.ui.AddPVAction;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
 *  When a Properties view is open and a SscanEditor is selected,
 *  a corresponding SscanPropertySheetPage is created.
 *
 *  It stays around even after the Properties is closed or changed
 *  to another editor, presumably to allow RCP to quickly re-display
 *  the properties when the editor is again selected.
 *
 *  The SscanPropertySheetPage is only disposed after the associated
 *  SscanEditor closes.
 *
 *  Under some circumstances, the SscanPropertySheetPage is even
 *  created for a closed-then-re-opened SscanEditor while there is
 *  no visible Properties view.
 *
 *  In summary, the 'Model' for this SscanPropertySheetPage never changes.
 *  Each SscanEditor.getAdapter will create a new SscanPropertySheetPage.
 *
 *  @author Kay Kasemir
 */
public class SscanPropertySheetPage extends Page
    implements IPropertySheetPage, ModelListener
{
    /** Model to display/edit in property sheet */
    final private Model model;

    /** Undo/redo operations manager */
    final private OperationsManager operations_manager;

    /** Top-level control for the property sheet */
    private Composite control;

    private Text formula_txt;

    private TableViewer trace_table;

    private Composite formula_panel;

    private Button rescales[];

    private ColorBlob background;

    /** Initialize
     *  @param model Model to display/edit
     */
    public SscanPropertySheetPage(final Model model,
            final OperationsManager operations_manager)
    {
        this.model = model;
        this.operations_manager = operations_manager;
    }

    /** {@inheritDoc} */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
        // NOP. Seems not to get called
    }

    /** {@inheritDoc} */
    @Override
    public Control getControl()
    {
        return control;
    }

    /** {@inheritDoc} */
    @Override
    public void createControl(final Composite parent)
    {
        control = new Composite(parent, 0);
        control.setLayout(new FillLayout());

        // Tabs: Traces, Time Axis, ...
        final TabFolder tab_folder = new TabFolder(control, SWT.TOP);
        createTracesTab(tab_folder);
        createValueAxesTab(tab_folder);
        createMiscTab(tab_folder);

        model.addListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        model.removeListener(this);
        super.dispose();
    }

    /** Create tab for traces (PVs, Formulas)
     *  @param tabs
     */
    private void createTracesTab(final TabFolder tabs)
    {
        // TabItem with SashForm
        final TabItem tab = new TabItem(tabs, 0);
        tab.setText(Messages.TracesTab);

        final SashForm sashform = new SashForm(tabs, SWT.VERTICAL | SWT.BORDER);
        sashform.setLayout(new FillLayout());
        tab.setControl(sashform);

        createTracesTabItemPanel(sashform);
        createTracesMenuAndToolbarActions();

        createTracesTabDetailPanel(sashform);

        trace_table.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                updateTracesTabDetailPanel();
            }
        });

        sashform.setWeights(new int[] { 60, 40 });
    }

    /** Within SashForm of the "Traces" tab, create the Model Item table
     *  @param sashform
     */
    private void createTracesTabItemPanel(final SashForm sashform)
    {
        // TableColumnLayout requires the TableViewer to be in its own Composite!
        final Composite model_item_top = new Composite(sashform, SWT.BORDER);
        final TableColumnLayout table_layout = new TableColumnLayout();
        model_item_top.setLayout(table_layout);
        trace_table = new TableViewer(model_item_top ,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
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
    private void createTracesTabDetailPanel(final SashForm sashform)
    {
        final Composite item_detail_top = new Composite(sashform, SWT.BORDER);
        item_detail_top.setLayout(new FormLayout());

        FormData fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);

        formula_panel = new Composite(item_detail_top, 0);
        fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        formula_panel.setLayoutData(fd);
        formula_panel.setLayout(new GridLayout(2, false));

        formula_txt = new Text(formula_panel, SWT.READ_ONLY | SWT.BORDER);
        formula_txt.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        formula_txt.setToolTipText(Messages.FormulaLabelEditTT);
        formula_txt.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseDown(MouseEvent e)
            {
                final Object item =
                    ((IStructuredSelection) trace_table.getSelection()).getFirstElement();
                if (! (item instanceof FormulaItem))
                    return;
                final FormulaItem formula = (FormulaItem) item;
                final EditFormulaDialog dlg =
                    new EditFormulaDialog(operations_manager, formula_txt.getShell(), formula);
                dlg.open();
            }

            @Override
            public void mouseUp(MouseEvent e) { /* NOP */ }
            @Override
            public void mouseDoubleClick(MouseEvent e) { /* NOP */ }
        });
        formula_panel.setVisible(false);
    }

    /** Update the lower 'detail' section of the "Traces" tab
     *  based on the currently selected item
     */
    private void updateTracesTabDetailPanel()
    {
        // Show PV or Formula Panel depending on selection
        final IStructuredSelection sel = (IStructuredSelection) trace_table.getSelection();
        if (sel.size() == 1)
        {
            final Object item = sel.getFirstElement();
            if (item instanceof ModelItem)
            {
                formula_panel.setVisible(false);
                return;
            }
            if (item instanceof FormulaItem)
            {
                final FormulaItem formula = (FormulaItem) item;
                formula_txt.setText(formula.getExpression());
                formula_panel.setVisible(true);
                return;
            }
        }
        // else: Neither PV nor formula, or multiple items selected
        formula_panel.setVisible(false);
    }

    /** Create context menu and toolbar actions for the traces table */
    private void createTracesMenuAndToolbarActions()
    {
        final MenuManager menu = new MenuManager();
        menu.setRemoveAllWhenShown(true);
        final Shell shell = trace_table.getControl().getShell();
        final AddPVAction add_pv = new AddPVAction(operations_manager, shell, model, false);
        final AddPVAction add_formula = new AddPVAction(operations_manager, shell, model, true);
        final DeleteItemsAction delete_pv = new DeleteItemsAction(operations_manager, trace_table, model);
        menu.addMenuListener(new IMenuListener()
        {
            @Override
            public void menuAboutToShow(IMenuManager manager)
            {
                menu.add(add_pv);
                menu.add(add_formula);
                menu.add(delete_pv);
                menu.add(new RemoveUnusedAxesAction(operations_manager, model));
                menu.add(new ExportXYAction(operations_manager,trace_table, model));
                final ModelItem pvs[] = getSelectedPVs();
                if (pvs.length <= 0)
                    return;
                menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

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

    /** @return Currently selected PVs in the table of traces (items). Never <code>null</code> */
    protected ModelItem[] getSelectedPVs()
    {
        final IStructuredSelection selection =
            (IStructuredSelection)trace_table.getSelection();
        if (selection.isEmpty())
            return new ModelItem[0];
        final Object obj[] = selection.toArray();
        final ArrayList<ModelItem> pvs = new ArrayList<ModelItem>();
        for (int i=0; i<obj.length; ++i)
            if (obj[i] instanceof ModelItem)
                pvs.add((ModelItem) obj[i]);
        return pvs.toArray(new ModelItem[pvs.size()]);
    }
   
    /** Create tab for traces (PVs, Formulas)
     *  @param tabs
     */
    private void createValueAxesTab(final TabFolder tab_folder)
    {
        final TabItem axes_tab = new TabItem(tab_folder, 0);
        axes_tab.setText(Messages.ValueAxes);

        final Composite parent = new Composite(tab_folder, 0);
        parent.setLayout(new GridLayout());

        // TableColumnLayout requires the TableViewer to be in its own Composite!
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);
        final AxesTableHandler ath = new AxesTableHandler(table_parent, table_layout, operations_manager);
        ath.getAxesTable().setInput(model);

        axes_tab.setControl(parent);
    }

    /** Create tab for misc. config items (update period, colors)
     *  @param tabs
     */
    private void createMiscTab(final TabFolder tab_folder)
    {
        final TabItem misc_tab = new TabItem(tab_folder, 0);
        misc_tab.setText("Misc."); //$NON-NLS-1$

        final Composite parent = new Composite(tab_folder, 0);
        parent.setLayout(new GridLayout(2, false));

        // Background Color: ______
        Label label = new Label(parent, 0);
        label.setText(Messages.BackgroundColorLbl);
        label.setLayoutData(new GridData());

        background = new ColorBlob(parent, model.getPlotBackground());
        background.setToolTipText(Messages.BackgroundColorTT);
        final GridData gd = new GridData();
        gd.minimumWidth = 80;
        gd.widthHint = 80;
        gd.heightHint = 15;
        background.setLayoutData(gd);
        background.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final ColorDialog dialog = new ColorDialog(parent.getShell());
                dialog.setRGB(model.getPlotBackground());
                final RGB value = dialog.open();
                if (value != null)
                    new ChangePlotBackgroundCommand(model, operations_manager, value);
            }
        });

        misc_tab.setControl(parent);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public void changedColors()
    {
        background.setColor(model.getPlotBackground());
    }

    /** {@inheritDoc} */
    @Override
    public void changedAxis(final AxisConfig axis)
    {
        // Axes Table handles this
    }
    
    /** {@inheritDoc} */
 //   @Override
 //   public void changedAxes(final AxesConfig axes)
 //   {
 //       // Axes Table handles this
//    }

    /** {@inheritDoc} */
    @Override
    public void itemAdded(final ModelItem item)
    {
        // Trace Table handles it
    }

    /** {@inheritDoc} */
    @Override
    public void itemRemoved(final ModelItem item)
    {
        // Trace Table handles it
    }

    /** {@inheritDoc} */
    @Override
    public void changedItemVisibility(final ModelItem item)
    {
        // Trace Table handles it
    }

    /** {@inheritDoc} */
    @Override
    public void changedItemLook(final ModelItem item)
    {
        updateTracesTabDetailPanel();
    }

    /** {@inheritDoc} */
    @Override
    public void changedItemDataConfig(final ModelItem item)
    {
        updateTracesTabDetailPanel();
    }

	@Override
	public void changedAnnotations() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedXYGraphConfig() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedItemData(ModelItem item) {
		// TODO Auto-generated method stub
		
	}
}
