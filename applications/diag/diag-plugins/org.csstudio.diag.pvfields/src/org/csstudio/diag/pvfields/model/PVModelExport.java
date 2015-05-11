/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.model;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.csstudio.diag.pvfields.PVField;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/** Export {@link PVModel} to file
 *  @author Dave Purcell - Original export code
 *  @author Kay Kasemir
 */
public class PVModelExport
{
	public static void export(final PVModel model, final Shell shell)
	{
		final FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setFilterNames(new String[] { "Tab-separated file" });
        dlg.setFilterExtensions(new String[] { "*.dat" });
        
        final String filename = dlg.open();
        export(model, shell, filename);
	}
	
	public static void export(final PVModel model, final Shell shell, final String filename)
	{
		if (filename == null)
			return;

		try
		{
			final PrintWriter out = new PrintWriter(filename);

			out.append("PV\t").append(model.getPVName()).println();
			out.println();
			
			out.println("Property\tValue");
			final Map<String, String> properties = model.getProperties();
			for (String prop : properties.keySet())
				out.append(prop).append("\t").println(properties.get(prop));
			out.println();

			out.println("Field\tOriginal Value\tCurrent Value");
			final List<PVField> fields = model.getFields();
			for (PVField field : fields)
				out.append(field.getName())
					.append("\t").append(field.getOriginalValue())
					.append("\t").println(field.getCurrentValue());

			out.close();
		}
		catch (Exception ex)
		{
			ExceptionDetailsErrorDialog.openError(shell, "Error", ex);
		}
	}
}
