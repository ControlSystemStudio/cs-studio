package org.csstudio.csdata.test;

import java.util.Arrays;

import org.csstudio.csdata.Device;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class DnDTestView extends ViewPart {
	// Data to transfer via Drag & Drop
	final ProcessVariable pv = new ProcessVariable("Fred");
	final ProcessVariable pv2 = new ProcessVariable("Barney");
	final Device device = new Device("SomeIOC");
	final DeviceAndAPV deviceAndAPV = new DeviceAndAPV(device, pv);
	final DeviceAndPVs deviceAndPVs = new DeviceAndPVs(device, Arrays.asList(pv, pv2));


	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.model.test.DnDTestView";

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public DnDTestView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		final GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		createPVExample(parent);
		createDeviceExample(parent);
		createDualExample(parent);
		createMultipleExample(parent);
	}

	/** PV: ... PV target */
	private void createPVExample(final Composite shell) {
		final Label l = new Label(shell, 0);
		l.setText("Drag " + pv.toString());
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Drag PV out of label
		new ControlSystemDragSource(l) {
			
			@Override
			public Object getSelection() {
				return pv;
			}
		};

		final Group pv_ctl = new Group(shell, 0);
		pv_ctl.setText("Drop PV (or text) here:");
		pv_ctl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Accept PV or text
		new ControlSystemDropTarget(pv_ctl, ProcessVariable.class,
				String.class) {
			@Override
			public void handleDrop(Object item) {
				if (item instanceof ProcessVariable) {
					pv_ctl.setText(((ProcessVariable) item)
							.getName());
				} else {
					pv_ctl.setText((String) item);
				}
			}
		};
	}

	/** Device: ... Device target */
	private void createDeviceExample(final Composite shell) {
		final Label l = new Label(shell, 0);
		l.setText("Drag " + device.toString());
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Drag device out of label
		new ControlSystemDragSource(l) {
			@Override
			public Object getSelection() {
				return device;
			}
		};

		final Group dvc_ctl = new Group(shell, 0);
		dvc_ctl.setText("Drop Device here:");
		dvc_ctl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Accept device
		new ControlSystemDropTarget(dvc_ctl, Device.class) {
			@Override
			public void handleDrop(final Object item) {
				dvc_ctl.setText("Device: "
						+ ((Device) item).getName());
			}
		};
	}

	/** Device-With-PV: ... Device-with-PV target */
	private void createDualExample(final Composite shell) {
		final Label l = new Label(shell, 0);
		l.setText("Drag device with PV");
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Drag device and pv out of label
		new ControlSystemDragSource(l) {
			@Override
			public Object getSelection() {
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
				ProcessVariable.class, Device.class) {
			@Override
			public void handleDrop(final Object item) {
				if (item instanceof ProcessVariable) {
					both_pv.setText(((ProcessVariable) item)
							.getName());
					both_device.setText("");
				} else if (item instanceof Device) {
					both_pv.setText("");
					both_device.setText(((Device) item).getName());
				} else if (item instanceof DeviceAndAPV) {
					both_pv.setText(((DeviceAndAPV) item).getPv()
							.getName());
					both_device.setText(((DeviceAndAPV) item).getDevice()
							.getName());
				}
				both_ctl.layout();
			}
		};
	}

	/** Device-With-PVs: ... Device-with-PVs target */
	private void createMultipleExample(final Composite shell) {
		final Label l = new Label(shell, 0);
		l.setText("Drag device with multiple PV");
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Drag device and pv out of label
		new ControlSystemDragSource(l) {
			@Override
			public Object getSelection() {
				return deviceAndPVs;
			}
		};

		final Group both_ctl = new Group(shell, 0);
		both_ctl.setText("Drop Device or PVs here:");
		both_ctl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		both_ctl.setLayout(new RowLayout(SWT.VERTICAL));

		final Label both_pv = new Label(both_ctl, 0);
		both_pv.setText("PVs?");

		final Label both_device = new Label(both_ctl, 0);
		both_device.setText("Device?");

		// Accept PV or device
		new ControlSystemDropTarget(both_ctl,  DeviceAndPVs.class,
				ProcessVariable[].class, Device.class) {
			@Override
			public void handleDrop(final Object item) {
				if (item instanceof ProcessVariable[]) {
					both_pv.setText(Arrays.toString((ProcessVariable[]) item));
					both_device.setText("");
				} else if (item instanceof Device) {
					both_pv.setText("");
					both_device.setText(((Device) item).getName());
				} else if (item instanceof DeviceAndPVs) {
					both_pv.setText(((DeviceAndPVs) item).getPvs().toString());
					both_device.setText(((DeviceAndPVs) item).getDevice()
							.getName());
				}
				both_ctl.layout();
			}
		};
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}