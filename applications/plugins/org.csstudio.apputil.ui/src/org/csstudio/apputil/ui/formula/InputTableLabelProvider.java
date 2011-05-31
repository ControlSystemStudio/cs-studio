/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.formula;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** The JFace label provider for a table with FormulaInput entries.
 *  @author Kay Kasemir
 */
public class InputTableLabelProvider extends LabelProvider implements
		ITableLabelProvider
{
    /** Get text for all but the 'select' column,
     *  where some placeholder is returned.
     */
	@Override
    public String getColumnText(Object obj, int index)
	{
        InputItem input = (InputItem) obj;
        return InputTableHelper.getText(input, index);
	}

    /** {@inheritDoc} */
	@Override
    public Image getColumnImage(Object obj, int index)
	{
        return null;
	}
}
