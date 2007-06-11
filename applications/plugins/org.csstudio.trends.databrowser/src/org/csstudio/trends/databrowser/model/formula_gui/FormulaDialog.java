package org.csstudio.trends.databrowser.model.formula_gui;

import org.csstudio.trends.databrowser.model.ModelItem;
import org.csstudio.trends.databrowser.model.formula_gui.InputTableHelper.Column;
import org.csstudio.util.swt.AutoSizeColumn;
import org.csstudio.util.swt.AutoSizeControlListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
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
 *  TODO externalize strings
 */
@SuppressWarnings("nls")
public class FormulaDialog extends Dialog
{
    private Text formula;
    
    /* For button: Add Text of pressed button to formula */
    final SelectionAdapter text_append_adapter = new SelectionAdapter()
    {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            Button b = (Button) e.getSource();
            formula.insert(b.getText());
        }
    };

    private TableViewer input_table;


    public FormulaDialog(Shell shell)
    {
        super(shell);
    }
    
    /** Set the dialog title. */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText("Formula Configuration");
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
        
        return box;
    }
    
    /** @return Formula section of dialog. */
    private Composite createFormularBox(final Composite parent)
    {
        Group box = new Group(parent, SWT.SHADOW_IN);
        box.setText("Formula");
        box.setLayout(new FillLayout());
        
        formula = new Text(box, 0);
        formula.setToolTipText("Enter formula");
        
        return box;
    }

    /** @return Table stuff for inputs. */
    private Composite createInputTable(final Composite parent)
    {
        Group box = new Group(parent, SWT.SHADOW_IN);
        box.setText("Inputs");

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
        
        // TODO remove this
        ModelItem.test_mode = true;
        InputTableItem data[] = new InputTableItem[]
        {
            new InputTableItem(new ModelItem(null, "fred", 0, 0, 0.0, 0.0, true, true, 0,
                                             0, 0, 0, null, false)),
            new InputTableItem(new ModelItem(null, "janet", 0, 0, 0.0, 0.0, true, true, 0,
                                             0, 0, 0, null, false)),
        };
        input_table.setInput(data);

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
        add.setText("Add Variable");
        add.setToolTipText("Add selected variable to formula");
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
            {
                final IStructuredSelection selection = 
                    (IStructuredSelection) input_table.getSelection();
                if (selection.size() != 1)
                    return;
                InputTableItem item = (InputTableItem)selection.getFirstElement();
                formula.insert(item.getVariableName());
            }
        });
        
        return box;
    }
    
    /** @return Extra keypad with functions. */
    private Composite createExtraKeypad(final Composite box)
    {
        Group extra = new Group(box, SWT.SHADOW_IN);
        extra.setText("Functions");
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        extra.setLayout(layout);
        
        /* [sqrt] [^]    [exp]
         * [sin]  [asin] [log]
         * [cos]  [acos] [abs] 
         * [tan]  [atan] [? :]
         * [PI]   [min]  [max]
         */
        addTextAppendButton(extra, "sqrt", "square root");
        addTextAppendButton(extra, " ^ ", "power");
        addTextAppendButton(extra, "exp", "exponential (base e)");
        // --
        addTextAppendButton(extra, "sin", "sine");
        addTextAppendButton(extra, "asin", "inverse sine");
        addTextAppendButton(extra, "log", "logarithm (base e)");
        // --
        addTextAppendButton(extra, "cos", "cosine");
        addTextAppendButton(extra, "acos", "inverse cosine");
        addTextAppendButton(extra, "abs", "absolute value");
        // --
        addTextAppendButton(extra, "tan", "tangent"); 
        addTextAppendButton(extra, "atan", "inverse tangent");
        addTextAppendButton(extra, " ? : ", "if-else");
        // --
        addTextAppendButton(extra, "PI", "number PI"); 
        addTextAppendButton(extra, "min", "minimum");
        addTextAppendButton(extra, "max", "maximum");
        return extra;
    }

    /** @return Basic number and plus/minus keypad */
    private Composite createBasicKeypad(final Composite box)
    {
        Group calc = new Group(box, SWT.SHADOW_IN);
        calc.setText("Basic Calculations");
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
        addButton(calc, "C", "Clear Formula", new GridData(),
                            new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {   formula.setText("");  }
        });
        addTextAppendButton(calc, "(", "Opening brace");
        addTextAppendButton(calc, ")", "Closing brace");
        addButton(calc, "<-", "Backspace", new GridData(),
                            new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            { 
                final String text = formula.getText();
                final int length = text.length();
                if (length >= 1)
                {
                    formula.setText(text.substring(0, length - 1));
                    formula.setSelection(length);
                }
            }
        });
        
        // --
        addTextAppendButton(calc, "7", "Number 7");
        addTextAppendButton(calc, "8", "Number 8");
        addTextAppendButton(calc, "9", "Number 9");
        addTextAppendButton(calc, "*", "Multiply");
        // --
        addTextAppendButton(calc, "4", "Number 4");
        addTextAppendButton(calc, "5", "Number 5");
        addTextAppendButton(calc, "6", "Number 6");
        addTextAppendButton(calc, "/", "Divide");
        // --
        addTextAppendButton(calc, "1", "Number 1");
        addTextAppendButton(calc, "2", "Number 2");
        addTextAppendButton(calc, "3", "Number 3");
        addTextAppendButton(calc, "+", "Add");
        // --
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        addButton(calc, "0", "Number 0", gd, text_append_adapter);
        addTextAppendButton(calc, ".", "Decimal Point");
        addTextAppendButton(calc, "-", "Substract");
        
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
