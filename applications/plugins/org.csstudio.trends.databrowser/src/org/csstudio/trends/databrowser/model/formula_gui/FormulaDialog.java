package org.csstudio.trends.databrowser.model.formula_gui;

import org.csstudio.trends.databrowser.model.FormulaInput;
import org.csstudio.trends.databrowser.model.FormulaModelItem;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.formula_gui.InputTableHelper.Column;
import org.csstudio.util.swt.AutoSizeColumn;
import org.csstudio.util.swt.AutoSizeControlListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/** GUI for a formula item.
 * 
 *  @author Kay Kasemir
 */
public class FormulaDialog extends Dialog
{
    private final FormulaModelItem formula_item;
    private Text formula_txt;
    private TableViewer input_table;
    
    /* For button: Add Text of pressed button to formula */
    final SelectionAdapter text_append_adapter = new SelectionAdapter()
    {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            Button b = (Button) e.getSource();
            formula_txt.insert(b.getText());
        }
    };


    /** Construct a dialog
     *  @param shell The parent shell
     *  @param formula_item The formula item to edit
     */
    public FormulaDialog(Shell shell, FormulaModelItem formula_item)
    {
        super(shell);
        this.formula_item = formula_item;
    }
    
    /** Set the dialog title. */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.getString("Formula_Title")); //$NON-NLS-1$
    }
    
    /** Create the GUI. */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite box = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) box.getLayout();
        layout.numColumns = 3;
        GridData gd;
        
        /* Formula: ____________________________
         * 
         * PVs     Variable   Extra keypad   Basic keypad
         * ------+---------    [sin] [cos]   [7] [8] [9]
         * fred  |                           [4] [5] [6]
         * freddy|  x                        [1] [2] [3]
         *            [Add]                  [   0 ] [.]
         */
        Composite form_box = createFormularBox(box);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        form_box.setLayoutData(gd);

        // Next row
        Composite variables = createInputTable(box);
        gd = new GridData();
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        variables.setLayoutData(gd);
        
        Composite extra = createExtraKeypad(box);
        gd = new GridData();
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        extra.setLayoutData(gd);
        
        Composite calc = createBasicKeypad(box);
        gd = new GridData();
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        calc.setLayoutData(gd);
        
        initializeGUI();
        
        return box;
    }

    /** Init. the GUI elements from the formula item. */
    private void initializeGUI()
    {
        formula_txt.setText(formula_item.getFormula());
        formula_txt.setSelection(formula_txt.getText().length());
        
        // List all the model's PVs as potential formula inputs
        final Model model = formula_item.getModel();
        final int model_N = model.getNumItems();
        InputTableItem data[] = new InputTableItem[model_N];
        for (int i=0; i<model_N; ++i)
        {
            final IModelItem item = model.getItem(i);
            String var_name = item.getName();
            // See if it's already associated with a variable in the formula
            for (int j=0; j<formula_item.getNumInputs(); ++j)
            {
                final FormulaInput input = formula_item.getInput(j);
                if (input.getModelItem() == item)
                {
                    var_name = input.getVariable().getName();
                    break;
                }
            }
            data[i] = new InputTableItem(item.getName(), var_name);
        }
        input_table.setInput(data);
    }
    
    /** @return Formula section of dialog. */
    private Composite createFormularBox(final Composite parent)
    {
        Group box = new Group(parent, SWT.SHADOW_IN);
        box.setText(Messages.getString("Formula_Formula")); //$NON-NLS-1$
        box.setLayout(new FillLayout());
        
        formula_txt = new Text(box, 0);
        formula_txt.setToolTipText(Messages.getString("Formula_Formula_TT")); //$NON-NLS-1$
        
        return box;
    }

    /** @return Table stuff for inputs. */
    private Composite createInputTable(final Composite parent)
    {
        Group box = new Group(parent, SWT.SHADOW_IN);
        box.setText(Messages.getString("Formula_Inputs")); //$NON-NLS-1$

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        box.setLayout(layout);
                
        Table table = new Table(box,
                            SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        final Column[] columns = InputTableHelper.Column.values();
        for (Column col : columns)
                AutoSizeColumn.make(table, col.getTitle(), col.getMinSize(),
                                    col.getWeight());
        // Configure table to auto-size the columns
        new AutoSizeControlListener(box, table);
        
        input_table = new TableViewer(table);
        input_table.setLabelProvider(new InputTableLabelProvider());
        input_table.setContentProvider(new ArrayContentProvider());
        
        // Allow editing
        CellEditor editors[] = new CellEditor[columns.length];
        editors[Column.INPUT_PV.ordinal()] = null;
        editors[Column.VARIABLE.ordinal()] = new TextCellEditor(table);
        
        String titles[] = new String[columns.length];
        for (int i=0; i<columns.length; ++i)
            titles[i] = columns[i].getTitle();
        input_table.setColumnProperties(titles);
        input_table.setCellEditors(editors);
        input_table.setCellModifier(new InputTableCellModifier(input_table));
        
        // new row
        Button add = new Button(box, SWT.PUSH);
        add.setText(Messages.getString("Formula_AddVar")); //$NON-NLS-1$
        add.setToolTipText(Messages.getString("Formula_AddVar_TT")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.TOP;
        add.setLayoutData(gd);
        
        // Add the variable name of the selected item to the formula
        add.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {   addSelectedVariable();        }
        });
        
        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {   addSelectedVariable();        }
        });
        
        return box;
    }
    
    /** Add the selected variable name in table to the formula */
    private void addSelectedVariable()
    {
        final IStructuredSelection selection = 
            (IStructuredSelection) input_table.getSelection();
        if (selection.size() != 1)
            return;
        InputTableItem item = (InputTableItem)selection.getFirstElement();
        formula_txt.insert(item.getVariableName());
    }

    /** @return Extra keypad with functions. */
    private Composite createExtraKeypad(final Composite box)
    {
        Group extra = new Group(box, SWT.SHADOW_IN);
        extra.setText(Messages.getString("Formula_Functions")); //$NON-NLS-1$
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        extra.setLayout(layout);
        
        /* [sqrt] [^]    [exp]
         * [sin]  [asin] [log]
         * [cos]  [acos] [abs] 
         * [tan]  [atan] [? :]
         * [PI]   [min]  [max]
         */
        addTextAppendButton(extra, "sqrt", Messages.getString("Formula_sqrt_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, " ^ ", Messages.getString("Formula_pwr_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, "exp", Messages.getString("Formula_exp_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        // --
        addTextAppendButton(extra, "sin", Messages.getString("Formula_sin_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, "asin", Messages.getString("Formula_asin_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, "log", Messages.getString("Formula_log_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        // --
        addTextAppendButton(extra, "cos", Messages.getString("Formula_cos_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, "acos", Messages.getString("Formula_acos_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, "abs", Messages.getString("Formula_abs_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        // --
        addTextAppendButton(extra, "tan", Messages.getString("Formula_tan_TT"));  //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, "atan", Messages.getString("Formula_atan_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, " ? : ", Messages.getString("Formula_if_else_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        // --
        addTextAppendButton(extra, "PI", Messages.getString("Formula_PI_TT"));  //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, "min", Messages.getString("Formula_min_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(extra, "max", Messages.getString("Formula_max_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        return extra;
    }

    /** @return Basic number and plus/minus keypad */
    private Composite createBasicKeypad(final Composite box)
    {
        Group calc = new Group(box, SWT.SHADOW_IN);
        calc.setText(Messages.getString("Formula_BasicCalcs")); //$NON-NLS-1$
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        calc.setLayout(layout);
        GridData gd;
        
        /* [C] [()] [<- ]
         * [7] [8] [9] [*]
         * [4] [5] [6] [/]
         * [1] [2] [3] [+]
         * [   0 ] [.] [-]
         */
        addButton(calc, Messages.getString("Formula_Clear"), Messages.getString("Formula_Clear_TT"), new GridData(), //$NON-NLS-1$ //$NON-NLS-2$
                            new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {   formula_txt.setText("");  } //$NON-NLS-1$
        });
        addTextAppendButton(calc, "(", Messages.getString("Formula_Open_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, ")", Messages.getString("Formula_Close_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addButton(calc, Messages.getString("Formula_Backspace"), Messages.getString("Formula_Backspace_TT"), new GridData(), //$NON-NLS-1$ //$NON-NLS-2$
                            new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            { 
                final String text = formula_txt.getText();
                final int length = text.length();
                if (length >= 1)
                {
                    formula_txt.setText(text.substring(0, length - 1));
                    formula_txt.setSelection(length);
                }
            }
        });
        
        // --
        addTextAppendButton(calc, "7", Messages.getString("Formula_7_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "8", Messages.getString("Formula_8_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "9", Messages.getString("Formula_9_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "*", Messages.getString("Formula_Mult_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        // --
        addTextAppendButton(calc, "4", Messages.getString("Formula_4_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "5", Messages.getString("Formula_5_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "6", Messages.getString("Formula_6_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "/", Messages.getString("Formula_Div_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        // --
        addTextAppendButton(calc, "1", Messages.getString("Formula_1_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "2", Messages.getString("Formula_2_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "3", Messages.getString("Formula_3_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "+", Messages.getString("Formula_Add_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        // --
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        addButton(calc, "0", Messages.getString("Formula_0_TT"), gd, text_append_adapter); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, ".", Messages.getString("Formula_Decimal_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        addTextAppendButton(calc, "-", Messages.getString("Formula_Sub_TT")); //$NON-NLS-1$ //$NON-NLS-2$
        
        return calc;
    }
    
    /** Add simple button for label and tooltip that adds its text to the 
     *  formula.
     */
    private void addTextAppendButton(final Composite parent,
                    final String label,
                    final String tooltip)
    {
        addButton(parent, label, tooltip,
                            new GridData(), text_append_adapter);
    }

    /** Add simple button. */
    private void addButton(final Composite parent,
                                     final String label,
                                     final String tooltip,
                                     GridData grid_data,
                                     SelectionListener listener)
    {
        Button b = new Button(parent, SWT.PUSH);
        b.setText(label);
        b.setToolTipText(tooltip);
        b.setLayoutData(grid_data);
        b.addSelectionListener(listener);
    }

    /** Update the formula item. */
    @Override
    protected void okPressed()
    {
        // TODO Auto-generated method stub
        super.okPressed();
    }
}
