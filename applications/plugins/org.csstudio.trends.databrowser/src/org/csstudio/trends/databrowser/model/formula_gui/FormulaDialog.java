package org.csstudio.trends.databrowser.model.formula_gui;

import java.util.ArrayList;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.FormulaInput;
import org.csstudio.trends.databrowser.model.FormulaModelItem;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.formula_gui.InputTableHelper.Column;
import org.csstudio.util.formula.Formula;
import org.csstudio.util.formula.VariableNode;
import org.csstudio.util.swt.AutoSizeColumn;
import org.csstudio.util.swt.AutoSizeControlListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/** GUI for a formula item.
 * 
 *  @author Kay Kasemir
 */
public class FormulaDialog extends Dialog
{
    /** The original formula item to edit. */
    private final FormulaModelItem formula_item;
    
    /** The formula text widget. */
    private Text formula_txt;
    
    /** Table viewer for FormulaInput items. */
    private TableViewer input_table;
    
    /** Status line widget. */
    private Label status;
    
    /** Color used when status is OK. */
    private Color status_color_error;

    /** Color used when status indicates an error. */
    private Color status_color_OK;

    /** For button: Add Text of pressed button to formula. */
    final SelectionAdapter text_append_adapter = new SelectionAdapter()
    {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            Button b = (Button) e.getSource();
            formula_txt.insert(b.getText());
        }
    };

    /** The parsed formula or <code>null</code> on errors. */
    private Formula formula;

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
        shell.setText(Messages.Formula_Title);
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
        
        status = new Label(box, 0);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        status.setLayoutData(gd);
        
        status_color_OK = status.getDisplay().getSystemColor(SWT.COLOR_RED);
        status_color_error = status.getDisplay().getSystemColor(SWT.COLOR_BLACK);
        
        initializeGUI();
        
        return box;
    }

    /** Init. the GUI elements from the formula item. */
    private void initializeGUI()
    {
        // List all the model's PVs as potential formula inputs
        final Model model = formula_item.getModel();
        final int model_N = model.getNumItems();
        FormulaInput input_items[] = new FormulaInput[model_N];
        for (int i=0; i<model_N; ++i)
        {
            final IModelItem item = model.getItem(i);
            VariableNode variable = null;
            // See if it's already associated with a variable in the formula
            for (int j=0; j<formula_item.getNumInputs(); ++j)
            {
                final FormulaInput input = formula_item.getInput(j);
                if (input.getModelItem() == item)
                {
                    variable = input.getVariable();
                    break;
                }
            }
            if (variable == null)
                variable = new VariableNode(item.getName());
            input_items[i] = new FormulaInput(item, variable);
        }
        input_table.setInput(input_items);
    }
    
    /** Create the buttons. */
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        super.createButtonsForButtonBar(parent);

        // Setting the formula text triggers parseFormula,
        // which in turn updates the OK button, and that is
        // only possible after the input_table is non-null,
        // and the button bar exists.
        // So hooking this up in here happens to work out OK,
        // while createDialogArea() would be too early.
        formula_txt.setText(formula_item.getFormula());
        formula_txt.setSelection(formula_txt.getText().length());
    }
    
    /** @return Formula section of dialog. */
    private Composite createFormularBox(final Composite parent)
    {
        Group box = new Group(parent, SWT.SHADOW_IN);
        box.setText(Messages.Formula_Formula);
        box.setLayout(new FillLayout());
        
        formula_txt = new Text(box, 0);
        formula_txt.setToolTipText(Messages.Formula_Formula_TT);
        formula_txt.addModifyListener(new ModifyListener()
        {
            /** Parse formula on every change. */
            public void modifyText(ModifyEvent e)
            {
                parseFormula();
            }
        });
        
        return box;
    }

    /** @return Table stuff for inputs. */
    private Composite createInputTable(final Composite parent)
    {
        Group box = new Group(parent, SWT.SHADOW_IN);
        box.setText(Messages.Formula_Inputs);

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
        add.setText(Messages.Formula_AddVar);
        add.setToolTipText(Messages.Formula_AddVar_TT);
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
        FormulaInput input = (FormulaInput)selection.getFirstElement();
        formula_txt.insert(input.getVariable().getName());
    }

    /** @return Extra keypad with functions. */
    private Composite createExtraKeypad(final Composite box)
    {
        Group extra = new Group(box, SWT.SHADOW_IN);
        extra.setText(Messages.Formula_Functions);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        extra.setLayout(layout);
        
        /* [sin]   [asin] [sqrt]  [^]
         * [cos]   [acos] [log]   [log10]
         * [tan]   [atan] [atan2] [exp]
         * [abs]   [min]  [max]   [? :]
         * [floor] [ceil] [PI]    [E]
         */
        addTextAppendButton(extra, "sin", Messages.Formula_sin_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "asin", Messages.Formula_asin_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "sqrt", Messages.Formula_sqrt_TT); //$NON-NLS-1$
        addTextAppendButton(extra, " ^ ", Messages.Formula_pwr_TT); //$NON-NLS-1$
        // --
        addTextAppendButton(extra, "cos", Messages.Formula_cos_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "acos", Messages.Formula_acos_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "log", Messages.Formula_log_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "log10", Messages.Formula_log10_TT); //$NON-NLS-1$
        // --
        addTextAppendButton(extra, "tan", Messages.Formula_tan_TT);  //$NON-NLS-1$
        addTextAppendButton(extra, "atan", Messages.Formula_atan_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "atan2", Messages.Formula_atan2_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "exp", Messages.Formula_exp_TT); //$NON-NLS-1$
        // --
        addTextAppendButton(extra, "abs", Messages.Formula_abs_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "min", Messages.Formula_min_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "max", Messages.Formula_max_TT); //$NON-NLS-1$
        addTextAppendButton(extra, " ? : ", Messages.Formula_if_else_TT); //$NON-NLS-1$
        // --
        addTextAppendButton(extra, "floor", Messages.Formula_floor_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "ceil", Messages.Formula_ceil_TT); //$NON-NLS-1$
        addTextAppendButton(extra, "PI", Messages.Formula_PI_TT);  //$NON-NLS-1$
        addTextAppendButton(extra, "E", Messages.Formula_E_TT);  //$NON-NLS-1$
        return extra;
    }

    /** @return Basic number and plus/minus keypad */
    private Composite createBasicKeypad(final Composite box)
    {
        Group calc = new Group(box, SWT.SHADOW_IN);
        calc.setText(Messages.Formula_BasicCalcs);
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
        addButton(calc, Messages.Formula_Clear, Messages.Formula_Clear_TT,
                  new GridData(),
                  new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {   formula_txt.setText("");  } //$NON-NLS-1$
        });
        
        addTextAppendButton(calc, "(", Messages.Formula_Open_TT); //$NON-NLS-1$
        addTextAppendButton(calc, ")", Messages.Formula_Close_TT); //$NON-NLS-1$
        addButton(calc, Messages.Formula_Backspace,
                        Messages.Formula_Backspace_TT,
                        new GridData(),
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
        addTextAppendButton(calc, "7", Messages.Formula_7_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "8", Messages.Formula_8_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "9", Messages.Formula_9_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "*", Messages.Formula_Mult_TT); //$NON-NLS-1$
        // --
        addTextAppendButton(calc, "4", Messages.Formula_4_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "5", Messages.Formula_5_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "6", Messages.Formula_6_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "/", Messages.Formula_Div_TT); //$NON-NLS-1$
        // --
        addTextAppendButton(calc, "1", Messages.Formula_1_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "2", Messages.Formula_2_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "3", Messages.Formula_3_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "+", Messages.Formula_Add_TT); //$NON-NLS-1$
        // --
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        addButton(calc, "0", Messages.Formula_0_TT, gd, text_append_adapter); //$NON-NLS-1$
        addTextAppendButton(calc, ".", Messages.Formula_Decimal_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "-", Messages.Formula_Sub_TT); //$NON-NLS-1$
        
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

    /** Parse the current text and variables into a formula,
     *  updating the <code>formula</code> and the status line.
     *  <p>
     *  Leaves <code>formula</code> as <code>null</code> in
     *  case of errors.
     *  @return <code>true</code> when formula parsed OK.
     */
    private boolean parseFormula()
    {
        // Create array of all variables actually found inside the formula
        FormulaInput input_items[] = (FormulaInput []) input_table.getInput();
        VariableNode vars[] = new VariableNode[input_items.length];
        for (int i=0; i<input_items.length; ++i)
            vars[i] = input_items[i].getVariable();
        // See if formula parses OK
        try
        {
            final String form = formula_txt.getText().trim();
            if (form.length() < 1)
                throw new Exception(Messages.Formula_EmptyFormulaError);
            formula = new Formula(form, vars);
        }
        catch (Exception ex)
        {
            status.setText(ex.getMessage());
            status.setForeground(status_color_OK);
            // Invalidate formula, disable the 'OK' button
            formula = null;
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return false;
        }
        // Formula parsed OK. Display the parsed result, enable 'OK' button.
        status.setText(Messages.Formula_ParsedFormula + formula.toString());
        status.setForeground(status_color_error);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return true;
    }
    
    /** Update the formula item. */
    @Override
    protected void okPressed()
    {
        if (formula == null)
            throw new Error("No formula?"); //$NON-NLS-1$
        
        
        // Get only the _used_ variables
        // Create array of all variables actually found inside the formula
        FormulaInput input_items[] = (FormulaInput []) input_table.getInput();
        ArrayList<VariableNode> used_vars = new ArrayList<VariableNode>();
        for (int i=0; i<input_items.length; ++i)
            if (formula.hasSubnode(input_items[i].getVariable()))
                used_vars.add(input_items[i].getVariable());
        // Copy into array
        VariableNode vars[] = new VariableNode[used_vars.size()];
        used_vars.toArray(vars);
        try
        {   // Create new formula with only the used variables
            formula = new Formula(formula.getFormula(), vars);
        }
        catch (Exception ex)
        {
            Plugin.logException("Formula won't convert", ex); //$NON-NLS-1$
        }
        
        // TODO update formula_item with new stuff
        
        super.okPressed();
    }
}
