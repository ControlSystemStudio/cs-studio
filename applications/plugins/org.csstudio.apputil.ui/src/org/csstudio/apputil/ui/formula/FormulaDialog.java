/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.formula;

import java.util.ArrayList;

import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.formula.VariableNode;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/** Dialog for editing a formula item.
 *  <p>
 *  The dialog can only be closed via 'OK' if the formula is valid.
 *
 *  @author Kay Kasemir
 */
public class FormulaDialog extends Dialog
{
    /** The original formula item to edit. */
    private final InputItem inputs[];

    /** The formula text. Updated while edited. */
    private String formula;

    /** The original formula item to edit. */
    private InputItem used_inputs[] = new InputItem[0];

    /** The formula text widget. */
    private Text formula_txt;

    /** Table viewer for FormulaInput items. */
    private TableViewer input_table;

    /** Status line widget. */
    private Label status;

    /** Color used when status indicates an error. */
    private Color status_color_error;

    /** For button: Add Text of pressed button to formula. */
    final private SelectionAdapter formula_text_appender = new SelectionAdapter()
    {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            Button b = (Button) e.getSource();
            formula_txt.insert(b.getText());
        }
    };

    /** Construct dialog for editing a formula
     *  @param shell The parent shell
     * @param formula Initial formula
     * @param inputs Possible inputs. Their variable names can be edited!
     */
    public FormulaDialog(final Shell shell, final String formula, final InputItem inputs[])
    {
        super(shell);
        this.formula = formula;
        this.inputs = inputs;
    }

    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** @return Formula. <code>null</code> when dialog didn't end via 'OK' */
    public String getFormula()
    {
        return formula;
    }

    /** @return InputItems used in the formula. Only valid when dialog ended via 'OK' */
    public InputItem[] getInputs()
    {
        return used_inputs;
    }

    /** Set the dialog title. */
    @Override
    protected void configureShell(final Shell shell)
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
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
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

        status_color_error = status.getDisplay().getSystemColor(SWT.COLOR_RED);

        return box;
    }

    /** Create the buttons. */
    @Override
    protected void createButtonsForButtonBar(final Composite parent)
    {
        super.createButtonsForButtonBar(parent);
        // At this time, the GUI including the buttons is available,
        // and we can parse the initial formula.
        // Since parse errors will update the 'OK' button, this
        // was impossible before the OK button actually existed.
        parseFormula();
    }

    /** @return Formula section of dialog. */
    private Composite createFormularBox(final Composite parent)
    {
        Group box = new Group(parent, SWT.SHADOW_IN);
        box.setText(Messages.Formula_Formula);
        box.setLayout(new FillLayout());

        formula_txt = new Text(box, 0);
        formula_txt.setToolTipText(Messages.Formula_Formula_TT);

        formula_txt.setText(formula);
        formula_txt.setSelection(formula.length());
        formula_txt.addModifyListener(new ModifyListener()
        {
            /** Parse formula on every change. */
            @Override
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
        final Group box = new Group(parent, SWT.SHADOW_IN);
        box.setText(Messages.Formula_Inputs);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        box.setLayout(layout);

        // TableColumnLayout requires table to be in its own composite.
        final Composite table_parent = new Composite(box, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);

        final Table table = new Table(table_parent,
                            SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setToolTipText(Messages.Formula_InputsTT);
        final InputTableHelper.Column[] columns = InputTableHelper.Column.values();
        for (InputTableHelper.Column col : columns)
        {
            final TableColumn c = new TableColumn(table, 0);
            c.setText(col.getTitle());
            c.setMoveable(true);
            c.setResizable(true);
            table_layout.setColumnData(c, new ColumnWeightData(col.getWeight(), col.getMinSize()));
        }

        input_table = new TableViewer(table);
        input_table.setLabelProvider(new InputTableLabelProvider());
        input_table.setContentProvider(new ArrayContentProvider());

        // Allow editing
        final CellEditor editors[] = new CellEditor[columns.length];
        editors[InputTableHelper.Column.INPUT.ordinal()] = null;
        editors[InputTableHelper.Column.VARIABLE.ordinal()] = new TextCellEditor(table);

        final String titles[] = new String[columns.length];
        for (int i=0; i<columns.length; ++i)
            titles[i] = columns[i].getTitle();
        input_table.setColumnProperties(titles);
        input_table.setCellEditors(editors);
        input_table.setCellModifier(new InputTableCellModifier(this, input_table));

        input_table.setInput(inputs);

        // new row
        final Button add = new Button(box, SWT.PUSH);
        add.setText(Messages.Formula_AddVar);
        add.setToolTipText(Messages.Formula_AddVar_TT);
        add.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

        // Enable only when PV is selected
        add.setEnabled(false);
        input_table.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(final SelectionChangedEvent event)
            {
                add.setEnabled(! input_table.getSelection().isEmpty());
            }
        });

        // Add the variable name of the selected item to the formula
        add.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                addSelectedVariable();
            }
        });

        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(final MouseEvent e)
            {
                addSelectedVariable();
            }
        });

        return box;
    }

    /** Add the selected variable name from input table to the formula */
    private void addSelectedVariable()
    {
        final IStructuredSelection selection =
            (IStructuredSelection) input_table.getSelection();
        if (selection.size() != 1)
            return;
        final InputItem input = (InputItem)selection.getFirstElement();
        formula_txt.insert(input.getVariableName());
    }

    /** @return Extra keypad with functions. */
    private Composite createExtraKeypad(final Composite box)
    {
        final Group extra = new Group(box, SWT.SHADOW_IN);
        extra.setText(Messages.Formula_Functions);
        extra.setLayout(new GridLayout(4, true));

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

        /* [C] [(] [)] [<- ]
         * [7] [8] [9] [*]
         * [4] [5] [6] [/]
         * [1] [2] [3] [+]
         * [  0  ] [.] [-]
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
        addButton(calc, "0", Messages.Formula_0_TT, gd, formula_text_appender); //$NON-NLS-1$
        addTextAppendButton(calc, ".", Messages.Formula_Decimal_TT); //$NON-NLS-1$
        addTextAppendButton(calc, "-", Messages.Formula_Sub_TT); //$NON-NLS-1$

        return calc;
    }

    /** Add simple button for label and tooltip that adds its text to the
     *  formula.
     */
    private void addTextAppendButton(final Composite parent,
            final String label, final String tooltip)
    {
        final GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        addButton(parent, label, tooltip,
                  gd, formula_text_appender);
    }

    /** Add simple button. */
    private void addButton(final Composite parent, final String label,
            final String tooltip, final GridData grid_data,
            final SelectionListener listener)
    {
        final Button b = new Button(parent, SWT.PUSH);
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
    boolean parseFormula()
    {
        // Create array of all available variables
        final VariableNode vars[] = new VariableNode[inputs.length];
        for (int i = 0; i < vars.length; ++i)
            vars[i] = new VariableNode(inputs[i].getVariableName());
        // See if formula parses OK
        final Formula formula;
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
            status.setForeground(status_color_error);
            // Invalidate formula, disable the 'OK' button
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            this.formula = null;
            return false;
        }
        // Formula parsed OK. Display the parsed result, enable 'OK' button.
        status.setText(NLS.bind(Messages.Formula_ParsedFormulaFmt, formula.toString()));
        status.setForeground(null);
        getButton(IDialogConstants.OK_ID).setEnabled(true);

        // Remember the formula's text and the _used_ inputs
        this.formula = formula.getFormula();
        // Create array of all variables actually found inside the formula
        final ArrayList<InputItem> used = new ArrayList<InputItem>();
        for (InputItem input : inputs)
            if (formula.hasSubnode(input.getVariableName()))
                used.add(input);
        // Convert to array
        used_inputs = used.toArray(new InputItem[used.size()]);

        return true;
    }

    /** Final consistency check when 'OK' is pressed */
    @Override
    protected void okPressed()
    {
        if (formula == null)
            throw new Error("No formula?"); //$NON-NLS-1$
        super.okPressed();
    }
}
