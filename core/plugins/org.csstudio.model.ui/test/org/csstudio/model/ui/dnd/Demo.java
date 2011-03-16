/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.model.ui.dnd;

import org.csstudio.model.DeviceName;
import org.csstudio.model.ProcessVariableName;
import org.csstudio.model.ui.dnd.ControlSystemDragSource;
import org.csstudio.model.ui.dnd.ControlSystemDropTarget;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-in Demo of Drag-and-Drop
 *
 *  Example for drag sources that provide a PV or device name, and drop targets
 *  that allow PV-or-String, assuming the string is a PV, or only allowing a
 *  device name, refusing anything else.
 *
 *  Must run with Eclipse platform because it relies on AdapterManager
 *
 *  @author Gabriele Carcassi
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Demo
{
	// Data to transfer via Drag & Drop
	final ProcessVariableName pv = new ProcessVariableName("Fred");
	final DeviceName device = new DeviceName("SomeIOC");
	final DeviceAndAPV deviceAndAPV = new DeviceAndAPV(new DeviceName("AnotherIOC"), new ProcessVariableName("Jane"));

	/** PV: ... PV target */
	private void createPVExample(final Shell shell)
	{
		final Label l = new Label(shell, 0);
		l.setText("Drag " + pv.toString());
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Drag PV out of label
		new ControlSystemDragSource(l)
		{
			@Override
			public Object getSelection()
			{
				return pv;
			}
		};

		final Group pv_ctl = new Group(shell, 0);
		pv_ctl.setText("Drop PV (or text) here:");
		pv_ctl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Accept PV or text
		new ControlSystemDropTarget(pv_ctl, ProcessVariableName.class,
				String.class)
		{
			@Override
			public void handleDrop(final Object item)
			{
				if (item instanceof ProcessVariableName)
				{
					pv_ctl.setText(((ProcessVariableName) item)
							.getProcessVariableName());
				}
				else
				{
					pv_ctl.setText((String) item);
				}
			}
		};
	}

	/** Device: ... Device target */
	private void createDeviceExample(final Shell shell)
	{
		final Label l = new Label(shell, 0);
		l.setText("Drag " + device.toString());
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Drag device out of label
		new ControlSystemDragSource(l)
		{
			@Override
			public Object getSelection()
			{
				return device;
			}
		};

		final Group dvc_ctl = new Group(shell, 0);
		dvc_ctl.setText("Drop Device here:");
		dvc_ctl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Accept device
		new ControlSystemDropTarget(dvc_ctl, DeviceName.class)
		{
			@Override
			public void handleDrop(final Object item)
			{
				dvc_ctl.setText("Device: "
						+ ((DeviceName) item).getDeviceName());
			}
		};
	}

	/** Device-With-PV: ... Device-with-PV target */
	private void createDualExample(final Shell shell)
	{
		final Label l = new Label(shell, 0);
		l.setText("Drag " + deviceAndAPV);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Drag device and pv out of label
		new ControlSystemDragSource(l)
		{
			@Override
			public Object getSelection()
			{
				return deviceAndAPV;
			}
		};

		final Group both_ctl = new Group(shell, 0);
		both_ctl.setText("Drop Device or PV here:");
		both_ctl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		both_ctl.setLayout(new RowLayout(SWT.VERTICAL));

		final Label both_pv = new Label(both_ctl, 0);
		both_pv.setText("PV?");

		final Label both_device = new Label(both_ctl, 0);
		both_device.setText("Device?");

		// Accept PV or device
		new ControlSystemDropTarget(both_ctl, DeviceAndAPV.class,
				DeviceName.class, ProcessVariableName.class)
		{
			@Override
			public void handleDrop(final Object item)
			{
				if (item instanceof ProcessVariableName)
				{
					both_pv.setText(((ProcessVariableName) item)
							.getProcessVariableName());
					both_device.setText("");
				}
				else if (item instanceof DeviceName)
				{
					both_pv.setText("");
					both_device.setText(((DeviceName) item).getDeviceName());
				}
				else if (item instanceof DeviceAndAPV)
				{
					both_pv.setText(((DeviceAndAPV) item).getPv()
							.getProcessVariableName());
					both_device.setText(((DeviceAndAPV) item).getDevice()
							.getDeviceName());
				}
				both_ctl.layout();
			}
		};
	}

	@Test
	public void runDemo()
	{
		// Display Setup
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setBounds(400, 100, 500, 350);
		final GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);

		// Could also register adapters via plugin.xml
        Platform.getAdapterManager().registerAdapters(new DeviceAndAPVAdapter(),
                DeviceAndAPV.class);

        createPVExample(shell);
        createDeviceExample(shell);
        createDualExample(shell);

		// Run GUI
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}
}
