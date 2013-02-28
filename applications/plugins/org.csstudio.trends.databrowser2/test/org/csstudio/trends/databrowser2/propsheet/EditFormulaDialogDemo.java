/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.csstudio.trends.databrowser2.model.FormulaInput;
import org.csstudio.trends.databrowser2.model.FormulaItem;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit plug-in demo SWT app for testing the Formula dialog.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EditFormulaDialogDemo
{
    @Test
    public void dialogDemo() throws Exception
    {
        final Shell shell = new Shell();

        // Demo model with some PVs and a formula
        final Model model = new Model();
        model.addItem(new PVItem("fred", 0.0));
        model.addItem(new PVItem("freddy", 0.0));
        model.addItem(new PVItem("jane", 0.0));
        model.addItem(new PVItem("janet", 0.0));

        final FormulaItem formula = new FormulaItem("demo", "2*x2",
        new FormulaInput[]
        {
            new FormulaInput(model.getItem(0), "x2"),
            new FormulaInput(model.getItem(3), "jj")
        });
        model.addItem(formula);

        final EditFormulaDialog edit = new EditFormulaDialog(null, shell, formula);

        System.out.println("Before editing:");
        dump(formula);


        if (edit.open())
        {
            System.out.println("After editing:");
            dump(formula);
        }
        else
            System.out.println("Cancelled");
    }

    private void dump(final FormulaItem formula)
    {
        final StringWriter buf = new StringWriter();
        final PrintWriter out = new PrintWriter(buf);
        formula.write(out);
        out.close();
        System.out.println(buf.toString());
    }
}
