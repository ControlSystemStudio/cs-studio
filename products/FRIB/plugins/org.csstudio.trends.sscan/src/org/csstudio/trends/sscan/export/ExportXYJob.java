/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.export;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.csstudio.archive.reader.SpreadsheetIterator;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/**
 * Ecipse Job for exporting data from Model to file
 * 
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExportXYJob extends PlainExportJob {
	private ModelItem[] items;

	public ExportXYJob(final Model model, final ModelItem[] items,
			final Source source, final int optimize_count,
			final ValueFormatter formatter, final String filename,
			final ExportErrorHandler error_handler) {
		super(model, source, optimize_count, formatter, filename, error_handler);
		this.items=items;

	}

	/** {@inheritDoc} */
	@Override
	protected void performExport(final IProgressMonitor monitor,
			final PrintStream out) throws Exception {

		for (ModelItem item : items) {
			SecureRandom random = new SecureRandom();
			File file = File.createTempFile(new BigInteger(12, random).toString(32), ".xls");
			FileWriter fwrite = new FileWriter(file);
			fwrite.write(item.getDisplayName()+"\r\n");
			fwrite.write("Number of Samples: "+Integer.valueOf(item.getSamples().getSize()).toString()+"\r\n");
			fwrite.write(item.getPositioner().getPositionerPV()+"	"+item.getDetector().getDetectorPV()+"\r\n");
			for (int i=0; i<item.getSamples().getSize();i++){
			  fwrite.write(Double.valueOf(item.getSamples().getSample(i).getXValue()).toString()+"	"+
					  Double.valueOf(item.getSamples().getSample(i).getYValue()).toString()+"\r\n");
			}
			fwrite.flush();
			fwrite.close();
			Desktop.getDesktop().open(file);
		}
	}
}
