/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.notifier.util.NotifierUtils;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Automated action edit dialog.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class EditAAItemDialog extends RowEditDialog {
	private Text titleText, delayText, detailsText;

	private Composite header;
	private Composite content;
	private Composite parent_composite;
	private Text messageLabel;
	private boolean showingError = false;

	protected EditAAItemDialog(final Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		parent_composite = (Composite) super.createDialogArea(parent);
		parent_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent_composite.setLayout(new GridLayout());
		
		// Header part
		header = new Composite(parent_composite, SWT.NONE);
		header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		header.setLayout(new GridLayout(2, false));
		header.setSize(0, 0);
		header.setVisible(false);
		
		// Content part
		content = new Composite(parent_composite, SWT.NONE);
		content.setLayout(new GridLayout(2, false));
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridData gd;

		final Label titleLabel = new Label(content, 0);
		titleLabel.setText(Messages.EditGDCItemDialog_Title);
		titleLabel.setLayoutData(new GridData());

		titleText = new Text(content, SWT.BORDER | SWT.SINGLE);
		titleText.setText(rowData[0]);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		titleText.setLayoutData(gd);

		final Label delayLabel = new Label(content, 0);
		delayLabel.setText(Messages.EditAAItemDialog_Delay);
		delayLabel.setLayoutData(new GridData());

		delayText = new Text(content, SWT.BORDER | SWT.SINGLE);
		delayText.setText(rowData[2]);
		delayText.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		delayText.setLayoutData(gd);

		final Label detailsLable = new Label(content, SWT.NONE);
		detailsLable.setText(Messages.EditGDCItemDialog_Detail);
		detailsLable.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		detailsText = new Text(content, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.widthHint = 300;
		gd.heightHint = 100;
		detailsText.setLayoutData(gd);
		detailsText.setText(rowData[1]);

		content.pack();
		parent_composite.pack();
		return parent_composite;
	}

	@SuppressWarnings("nls")
	@Override
	protected void okPressed() {
		rowData[0] = titleText == null ? "" : titleText.getText().trim();
		rowData[1] = detailsText == null ? "" : detailsText.getText().trim();
		rowData[2] = delayText == null ? "" : delayText.getText().trim();
		try {
			if (rowData[1] != null && !rowData[1].isEmpty()) {
				NotifierUtils.performValidation(rowData[1]);
			}
			showingError = false;
			super.okPressed();
		} catch (Exception e) {
			if(!showingError) {
				// Message image @ left
				final Label messageImageLabel = new Label(header, SWT.NONE);
				messageImageLabel.setImage(JFaceResources.getImage(DLG_IMG_MESSAGE_ERROR));
				GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
				gridData.widthHint = 30;
				messageImageLabel.setLayoutData(gridData);
				messageImageLabel.pack();
				// Message label @ right
				messageLabel = new Text(header, SWT.WRAP);
				messageLabel.setBackground(header.getBackground());
				messageLabel.setForeground(parent_composite.getDisplay().getSystemColor(SWT.COLOR_RED));
				messageLabel.setFont(JFaceResources.getDialogFont());
				header.setVisible(true);
				showingError = true;
			}
			String message = e.getMessage();
			// Cut long messages
			GC gc = new GC(messageLabel);
			int charWidth = gc.getFontMetrics().getAverageCharWidth();
			int parentWidth = parent_composite.getSize().x;
			int msgLength = message.length();
			int parentMaxLength = (parentWidth - 70) / charWidth;
			if (msgLength > parentMaxLength) {
				int nbLines = (msgLength / parentMaxLength) + 1;
				StringBuilder sb = new StringBuilder();
				int index = 0;
				while (index < nbLines) {
					int beginIndex = index * parentMaxLength;
					int endIndex = (index + 1) * parentMaxLength;
					if (endIndex > msgLength) endIndex = msgLength;
					sb.append(message.substring(beginIndex, endIndex));
					if (index < nbLines - 1) sb.append("\n");
					index++;
				}
				message = sb.toString();
			}
			messageLabel.setText(message);
			messageLabel.pack();
			header.pack();
			parent_composite.layout();
			getShell().layout();
		}
	}

}
