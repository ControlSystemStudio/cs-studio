package org.csstudio.model.test;


import java.util.Arrays;

import org.csstudio.model.DeviceName;
import org.csstudio.model.ProcessVariableName;
import org.csstudio.model.ui.dnd.ControlSystemDragSource;
import org.csstudio.model.ui.dnd.ControlSystemDropTarget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;


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
	final ProcessVariableName pv = new ProcessVariableName("Fred");
	final ProcessVariableName pv2 = new ProcessVariableName("Barney");
	final DeviceName device = new DeviceName("SomeIOC");
	final DeviceAndAPV deviceAndAPV = new DeviceAndAPV(device, pv);
	final DeviceAndPVs deviceAndPVs = new DeviceAndPVs(device, Arrays.asList(pv, pv2));


	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.model.test.DnDTestView";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

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
		new ControlSystemDropTarget(pv_ctl, ProcessVariableName.class,
				String.class) {
			@Override
			public void handleDrop(Object item) {
				if (item instanceof ProcessVariableName) {
					pv_ctl.setText(((ProcessVariableName) item)
							.getProcessVariableName());
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
		new ControlSystemDropTarget(dvc_ctl, DeviceName.class) {
			@Override
			public void handleDrop(final Object item) {
				dvc_ctl.setText("Device: "
						+ ((DeviceName) item).getDeviceName());
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
				ProcessVariableName.class, DeviceName.class) {
			@Override
			public void handleDrop(final Object item) {
				if (item instanceof ProcessVariableName) {
					both_pv.setText(((ProcessVariableName) item)
							.getProcessVariableName());
					both_device.setText("");
				} else if (item instanceof DeviceName) {
					both_pv.setText("");
					both_device.setText(((DeviceName) item).getDeviceName());
				} else if (item instanceof DeviceAndAPV) {
					both_pv.setText(((DeviceAndAPV) item).getPv()
							.getProcessVariableName());
					both_device.setText(((DeviceAndAPV) item).getDevice()
							.getDeviceName());
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
				ProcessVariableName[].class, DeviceName.class) {
			@Override
			public void handleDrop(final Object item) {
				System.out.println(item.getClass());
				if (item instanceof ProcessVariableName[]) {
					both_pv.setText(Arrays.toString((ProcessVariableName[]) item));
					both_device.setText("");
				} else if (item instanceof DeviceName) {
					both_pv.setText("");
					both_device.setText(((DeviceName) item).getDeviceName());
				} else if (item instanceof DeviceAndPVs) {
					both_pv.setText(((DeviceAndPVs) item).getPvs().toString());
					both_device.setText(((DeviceAndPVs) item).getDevice()
							.getDeviceName());
				}
				both_ctl.layout();
			}
		};
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				DnDTestView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Drag'n'Drop Test View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}