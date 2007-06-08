package org.csstudio.trends.databrowser.model.formula_gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
            formula.setText(formula.getText() + b.getText());
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
        
        /* Formula: ____________________
         * 
         * PVs     Variable  Add   [C] [(] [)] [*]
         *                         [7] [8] [9] [/]
         * fred                    [4] [5] [6] [+]
         * freddy  x               [1] [2] [3] [+]
         *                         [   0 ] [.] [-]
         */
        
        Label l = new Label(box, 0);
        l.setText("Formula:");
        gd = new GridData();
        l.setLayoutData(gd);
        
        formula = new Text(box, 0);
        formula.setToolTipText("Enter formula");
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns-1;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        l.setLayoutData(gd);
        
        Composite calc = createCalcKeys(box);
        gd = new GridData();
        calc.setLayoutData(gd);
        
        return box;
    }

    private Composite createCalcKeys(final Composite box)
    {
        Composite calc = new Composite(box, 0);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        calc.setLayout(layout);
        GridData gd;
        
        Button b = new Button(calc, SWT.PUSH);
        b.setText("C");
        b.setToolTipText("Clear Formula");
        b.setLayoutData(new GridData());
        b.addSelectionListener(new SelectionAdapter()
        {
            /** Clear formula */
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                formula.setText("");
            }
        });
 
        addTextAppendButton(calc, "(", "Opening brace");
        addTextAppendButton(calc, ")", "Closing brace");
        addTextAppendButton(calc, "*", "Multiply");

        // --
        addTextAppendButton(calc, "7", "Number 7");
        addTextAppendButton(calc, "8", "Number 8");
        addTextAppendButton(calc, "9", "Number 9");
        addTextAppendButton(calc, "/", "Divide");

        // --
        addTextAppendButton(calc, "4", "Number 4");
        addTextAppendButton(calc, "5", "Number 5");
        addTextAppendButton(calc, "6", "Number 6");
        gd = new GridData();
        gd.verticalSpan = 2;
        gd.verticalAlignment = SWT.FILL;
        addTextAppendButton(calc, "+", "Add", gd);

        // --
        addTextAppendButton(calc, "1", "Number 1");
        addTextAppendButton(calc, "2", "Number 2");
        addTextAppendButton(calc, "3", "Number 3");
        // ... and the lower half of "+"
        
        // --
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        addTextAppendButton(calc, "0", "Number 0", gd);
        addTextAppendButton(calc, ".", "Decimal Point");
        addTextAppendButton(calc, "-", "Substract");

        return calc;
    }
    
    private void addTextAppendButton(final Composite parent,
                    final String label,
                    final String tooltip)
    {
        addTextAppendButton(parent, label, tooltip, new GridData());
    }

    private void addTextAppendButton(final Composite parent,
                                     final String label,
                                     final String tooltip,
                                     GridData grid_data)
    {
        Button b = new Button(parent, SWT.PUSH);
        b.setText(label);
        b.setToolTipText(tooltip);
        b.setLayoutData(grid_data);
        b.addSelectionListener(text_append_adapter);
    }

    /** Update the formula item. */
    @Override
    protected void okPressed()
    {
        // TODO Auto-generated method stub
        super.okPressed();
    }
}
