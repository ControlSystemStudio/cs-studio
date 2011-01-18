package org.csstudio.display.waterfall;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.csstudio.utility.pvmanager.ui.widgets.WaterfallComposite;

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

public class WaterfallView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.display.waterfall.WaterfallView";

	/**
	 * The constructor.
	 */
	public WaterfallView() {
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		
		Label lblPvName = new Label(parent, SWT.NONE);
		FormData fd_lblPvName = new FormData();
		fd_lblPvName.top = new FormAttachment(0, 13);
		fd_lblPvName.left = new FormAttachment(0, 10);
		lblPvName.setLayoutData(fd_lblPvName);
		lblPvName.setText("PV Name:");
		
		ComboViewer comboViewer = new ComboViewer(parent, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 10);
		fd_combo.left = new FormAttachment(lblPvName, 6);
		fd_combo.right = new FormAttachment(100, -10);
		combo.setLayoutData(fd_combo);
		
		final WaterfallComposite waterfallComposite = new WaterfallComposite(parent, SWT.NONE);
		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.bottom = new FormAttachment(100, -10);
		fd_waterfallComposite.left = new FormAttachment(0, 10);
		fd_waterfallComposite.top = new FormAttachment(combo, 6);
		fd_waterfallComposite.right = new FormAttachment(combo, 0, SWT.RIGHT);
		waterfallComposite.setLayoutData(fd_waterfallComposite);
		
		ComboHistoryHelper name_helper = new ComboHistoryHelper(Activator.getDefault()
				.getDialogSettings(), "pv_list", comboViewer) {
			@Override
			public void newSelection(final String pv_name) {
				waterfallComposite.setPvName(pv_name);
			}
		};
	}
}