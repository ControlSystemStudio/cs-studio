package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditAAItemDialog extends RowEditDialog
{
    private Text titleText, delayText, detailsText;

	protected EditAAItemDialog(final Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite parent_composite = (Composite) super
				.createDialogArea(parent);
		final Composite composite = new Composite(parent_composite, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, false));
		GridData gd;

		final Label titleLabel = new Label(composite, 0);
		titleLabel.setText(Messages.EditGDCItemDialog_Title);
		titleLabel.setLayoutData(new GridData());

		titleText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		titleText.setText(rowData[0]);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		titleText.setLayoutData(gd);
		
		final Label delayLabel = new Label(composite, 0);
		delayLabel.setText(Messages.EditAAItemDialog_Delay);
		delayLabel.setLayoutData(new GridData());

		delayText = new Text(composite, SWT.BORDER | SWT.SINGLE);
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

		final Label detailsLable = new Label(composite, SWT.NONE);
		detailsLable.setText(Messages.EditGDCItemDialog_Detail);
		detailsLable.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,
				false, false));

		detailsText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.widthHint = 300;
		gd.heightHint = 100;
		detailsText.setLayoutData(gd);
		detailsText.setText(rowData[1]);

		return parent_composite;
	}

	@SuppressWarnings("nls")
	@Override
	protected void okPressed() {
		rowData[0] = titleText == null ? "" : titleText.getText().trim();
		rowData[1] = detailsText == null ? "" : detailsText.getText().trim();
		rowData[2] = delayText == null ? "" : delayText.getText().trim();
		super.okPressed();
	}

}
