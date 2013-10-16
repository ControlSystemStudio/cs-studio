/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.ui.scantree.model.ScanTreeModel;
import org.csstudio.scan.ui.scantree.operations.InsertOperation;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

/** JUnit test of the {@link InsertOperation}
 *  @author Kay Kasemir
 */
public class InsertOperationUnitTest
{
	@Test
	public void testExecuteIProgressMonitorIAdaptable() throws Exception
	{
		final ScanTreeModel model = new ScanTreeModel();
		model.setCommands(DemoScan.createCommands());
		final List<ScanCommand> scan = model.getCommands();
		final int orig_size = scan.size();

		List<ScanCommand> to_add = new ArrayList<ScanCommand>();
		to_add.add(new DelayCommand(1.0));
		to_add.add(new DelayCommand(2.0));
		to_add.add(new DelayCommand(3.0));
		// When inserting several elements _before_ an element,
		// a bug reversed their order
		InsertOperation insert = new InsertOperation(model, scan.get(0), to_add , false);
		insert.execute(new NullProgressMonitor(), null);

		XMLCommandWriter.write(System.out, scan);
		assertEquals(orig_size + 3, scan.size());

		assertSame(DelayCommand.class, scan.get(0).getClass());
		assertSame(DelayCommand.class, scan.get(1).getClass());
		assertSame(DelayCommand.class, scan.get(2).getClass());

		// Assert correct order
		assertEquals(1.0, ((DelayCommand) scan.get(0)).getSeconds(), 0.1);
		assertEquals(2.0, ((DelayCommand) scan.get(1)).getSeconds(), 0.1);
		assertEquals(3.0, ((DelayCommand) scan.get(2)).getSeconds(), 0.1);
	}
}
