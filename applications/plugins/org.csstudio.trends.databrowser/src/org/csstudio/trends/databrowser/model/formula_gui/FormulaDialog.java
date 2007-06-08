package org.csstudio.trends.databrowser.model.formula_gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
         * PVs     Variable  Add  Extra keypad   Basic keypad
         *                        [sin] [cos]   [7] [8] [9]
         * fred                                 [4] [5] [6]
         * freddy  x                            [1] [2] [3]
         *                                      [   0 ] [.]
         */
        Composite form_box = createFormularBox(box);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        form_box.setLayoutData(gd);

        // Next row
        Composite variables = createBasicKeypad(box);
        gd = new GridData();
        variables.setLayoutData(gd);
        
        Composite extra = createExtraKeypad(box);
        gd = new GridData();
        extra.setLayoutData(gd);
        
        Composite calc = createBasicKeypad(box);
        gd = new GridData();
        calc.setLayoutData(gd);
        
        return box;
    }
    
    private Composite createFormularBox(final Composite parent)
    {
        Composite box = new Group(parent, SWT.SHADOW_IN);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        box.setLayout(layout);
        
        Label l = new Label(box, 0);
        l.setText("Formula:");
        GridData gd = new GridData();
        l.setLayoutData(gd);
        
        formula = new Text(box, 0);
        formula.setToolTipText("Enter formula");
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        formula.setLayoutData(gd);
        
        return box;
    }
    
    /** @return Extra keypad with formulas */
    private Composite createExtraKeypad(final Composite box)
    {
        Composite extra = new Group(box, SWT.SHADOW_IN);
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
        Composite calc = new Group(box, SWT.SHADOW_IN);
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
