/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.content;

import java.awt.Dimension;
import java.util.List;

import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.tooltips.TooltipContent;
import org.csstudio.autocomplete.tooltips.TooltipData;
import org.csstudio.autocomplete.tooltips.TooltipDataHandler;
import org.csstudio.autocomplete.ui.AutoCompleteUIPlugin;
import org.csstudio.autocomplete.ui.util.SSStyledText;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The popup used to display tooltips.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class ContentHelperPopup extends PopupDialog {

	private final class PopupCloserListener extends SelectionAdapter implements Listener {

		public void handleEvent(final Event e) {
			// If focus is leaving an important widget or the field's
			// shell is deactivating
			if (e.type == SWT.FocusOut) {
				/*
				 * Ignore this event if it's only happening because focus is
				 * moving between the helper shells or their controls. Do this
				 * in an asynchronous way since the focus is not actually
				 * switched when this event is received.
				 */
				e.display.asyncExec(new Runnable() {
					public void run() {
						if (isValid()) {
							if (hasFocus()) {
								return;
							}
							// Workaround a problem on X and Mac, whereby at
							// this point, the focus control is not known.
							// This can happen, for example, when resizing
							// the helper shell on the Mac.
							// Check the active shell.
							Shell activeShell = e.display.getActiveShell();
							if (activeShell == getShell()) {
								return;
							}
							close();
						}
					}
				});
				return;
			}
			if (e.type == SWT.Resize) {
				// Do not close helper on resize for web version.
				// RAP raise too many resize event
				return;
			}
			// For all other events, merely getting them dictates closure.
			close();
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			close();
		}

		// Install the listeners for events that need to be monitored for
		// helper closure.
		void installListeners() {
			// Listeners on this popup's shell
			getShell().addListener(SWT.Deactivate, this);
			getShell().addListener(SWT.Close, this);

			// Listeners on the target control
			control.addListener(SWT.MouseDoubleClick, this);
			control.addListener(SWT.MouseDown, this);
			control.addListener(SWT.Dispose, this);
			control.addListener(SWT.FocusOut, this);
			// Listeners on the target control's shell
			Shell controlShell = control.getShell();
			controlShell.addListener(SWT.Move, this);
			controlShell.addListener(SWT.Resize, this);
			
			control.addListener(SWT.DefaultSelection, this);
			if (control instanceof Text)
				((Text) control).addSelectionListener(this);
			if (control instanceof Combo)
				((Combo) control).addSelectionListener(this);
		}

		// Remove installed listeners
		void removeListeners() {
			if (isValid()) {
				getShell().removeListener(SWT.Deactivate, this);
				getShell().removeListener(SWT.Close, this);
			}
			if (control != null && !control.isDisposed()) {
				control.removeListener(SWT.MouseDoubleClick, this);
				control.removeListener(SWT.MouseDown, this);
				control.removeListener(SWT.Dispose, this);
				control.removeListener(SWT.FocusOut, this);

				Shell controlShell = control.getShell();
				controlShell.removeListener(SWT.Move, this);
				controlShell.removeListener(SWT.Resize, this);
				
				control.removeListener(SWT.DefaultSelection, this);
				if (control instanceof Text)
					((Text) control).removeSelectionListener(this);
				if (control instanceof Combo)
					((Combo) control).removeSelectionListener(this);
			}
		}
	}

	private Dimension margins = new Dimension(8, 8);
	private boolean isOpened = false;
	private boolean canOpen = true;

	/*
	 * The listener installed in order to close the helper.
	 */
	private PopupCloserListener popupCloser;

	/*
	 * The control for which content helper is provided.
	 */
	private Control control;

	/*
	 * The text controls that displays the text.
	 */
	private SSStyledText text;

	/*
	 * The data handler used to generate helper content.
	 */
	private TooltipDataHandler dataHandler;

	/*
	 * The content to show in the helper.
	 */
	private TooltipContent content;

	private ContentProposalAdapter adapter;

	/*
	 * Construct an helper with the specified parent.
	 */
	public ContentHelperPopup(ContentProposalAdapter adapter) {
		super(adapter.getControl().getShell(), SWT.NO_TRIM | SWT.ON_TOP, false,
				false, false, false, false, null, null);
		this.adapter = adapter;
		this.control = adapter.getControl();
		this.dataHandler = new TooltipDataHandler();
	}

	/*
	 * Create a text control for showing the helper.
	 */
	protected Control createDialogArea(Composite parent) {
		// Use the compact margins employed by PopupDialog.
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalIndent = margins.width;
		gd.verticalIndent = margins.height;
		text = AutoCompleteUIPlugin.getUIHelper().newStyledText();
		Control c = text.init(parent, SWT.MULTI | SWT.READ_ONLY | SWT.NO_FOCUS, gd);
		updateDisplay();
		return c;
	}

	private void updateDisplay() {
		if (content == null || !isValid())
			return;
		text.setText(content.value);
		for (ProposalStyle ps : content.styles) {
			Color color = control.getDisplay().getSystemColor(ps.fontColor);
			text.setStyle(color, ps.fontStyle, ps.from, ps.to - ps.from);
		}
	}

	/*
	 * Adjust the bounds so that we appear on the top of the control.
	 */
	protected void adjustBounds() {
		// Get our control's location in display coordinates.
		Point location = control.getDisplay().map(control.getParent(), null,
				control.getLocation());
		int controlX = location.x;
		int controlY = location.y;
		int controlWidht = control.getBounds().width;
		int controlHeight = control.getBounds().height;

		if (content != null) {
			Point size = text.getSize();
			controlWidht = size.x + margins.width * 2;
			controlHeight = size.y + margins.height * 2;
		}

		Rectangle parentBounds = new Rectangle(controlX, controlY,
				controlWidht, controlHeight);

		// Try placing the helper on the top
		final Rectangle proposedBounds = new Rectangle(parentBounds.x
				+ PopupDialog.POPUP_HORIZONTALSPACING, parentBounds.y
				- parentBounds.height - PopupDialog.POPUP_VERTICALSPACING,
				parentBounds.width, parentBounds.height);

		// Constrain to the display
		Rectangle constrainedBounds = getConstrainedShellBounds(proposedBounds);

		// If it won't fit on the top, try the bottom
		if (constrainedBounds.intersects(parentBounds)) {
			proposedBounds.y = parentBounds.y + parentBounds.height
					+ PopupDialog.POPUP_VERTICALSPACING;
		}
		proposedBounds.x = constrainedBounds.x;

		getShell().setBounds(proposedBounds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.PopupDialog#getForeground()
	 */
	protected Color getForeground() {
		return control.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.PopupDialog#getBackground()
	 */
	protected Color getBackground() {
		return control.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
	}

	/**
	 * Opens this ContentHelperPopup
	 * 
	 * @return the return code
	 * 
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open() {
		if (!canOpen)
			return 0;
		String fieldContent = adapter.getControlContentAdapter()
				.getControlContents(control);
		content = dataHandler.generateTooltipContent(fieldContent);
		if (content == null) {
			isOpened = false;
			return Window.CANCEL;
		}
		int value = super.open();
		if (popupCloser == null) {
			popupCloser = new PopupCloserListener();
		}
		popupCloser.installListeners();
		isOpened = true;
		return value;
	}

	/**
	 * Closes this ContentHelperPopup.
	 * 
	 * @return <code>true</code> if the window is (or was already) closed, and
	 *         <code>false</code> if it is still open
	 */
	public boolean close() {
		if (!isOpened)
			return false;
		text.dispose();
		popupCloser.removeListeners();
		isOpened = false;
		return super.close();
	}

	public boolean close(boolean canOpen) {
		this.canOpen = canOpen;
		return close();
	}

	/**
	 * Refresh this ContentHelperPopup if already opened.
	 * 
	 * @return <code>true</code> if the window was refreshed, and
	 *         <code>false</code> if not
	 */
	public boolean refresh() {
		if (!isOpened)
			return false;
		String fieldContent = adapter.getControlContentAdapter()
				.getControlContents(control);
		content = dataHandler.generateTooltipContent(fieldContent);
		if (content == null) {
			close();
			return true;
		}
		updateDisplay();
		adjustBounds();
		return true;
	}

	/*
	 * Return whether the helper is opened.
	 */
	public boolean isOpened() {
		return isOpened;
	}

	private boolean hasFocus() {
		return text.hasFocus();
	}

	private boolean isValid() {
		return text.isValid();
	}

	public void updateData(List<TooltipData> dataList) {
		if (dataList == null)
			return;
		for (TooltipData data : dataList)
			dataHandler.addData(data);
		if (control != null && !control.isDisposed())
			control.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!refresh())
						open();
				}
			});
	}

	public void clearData() {
		dataHandler.clearData();
	}

}
