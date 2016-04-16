/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.formula;

import junit.framework.Assert;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/**
 * JUnit test /demo of the Formula dialog.
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FormulaDialogDemo
{
    @Test
    public void dialogDemo() throws Exception
    {
        final Shell shell = new Shell();

        final InputItem inputs[] = new InputItem[] { new InputItem("fred", "x"),
                                                    new InputItem("freddy", "y"),
                                                    new InputItem("jane", "z"),
                                                    new InputItem("janet", "jj"), };

        final FormulaDialog dialog = new FormulaDialog(shell, "2*x + y", inputs);
        for (final InputItem input : dialog.getInputs())
            Assert.assertEquals(input.getVariableName(), input.getInputName());

        dialog.open();
    }
}
