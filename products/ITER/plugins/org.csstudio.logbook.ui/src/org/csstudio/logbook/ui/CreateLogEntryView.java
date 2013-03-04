/**
 * 
 */
package org.csstudio.logbook.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * @author shroffk
 * 
 */
public class CreateLogEntryView extends ViewPart {
	private LogEntryWidget logEntryWidget;

	public CreateLogEntryView() {
	}

	/** View ID defined in plugin.xml */
	public static final String ID = "org.csstudio.logbook.ui.CreateLogEntry"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		logEntryWidget = new LogEntryWidget(parent, SWT.NONE, false, true);
		FormData fd_logEntryWidget = new FormData();
		fd_logEntryWidget.top = new FormAttachment(0, 1);
		fd_logEntryWidget.left = new FormAttachment(0, 1);
		fd_logEntryWidget.bottom = new FormAttachment(100, -1);
		fd_logEntryWidget.right = new FormAttachment(100, -1);
		logEntryWidget.setLayoutData(fd_logEntryWidget);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}

}
