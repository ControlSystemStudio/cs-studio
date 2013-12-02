package org.csstudio.shift.ui;


import org.csstudio.shift.ui.ShiftWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class CreateShiftView extends ViewPart {
	private ShiftWidget shiftWidget;

	public CreateShiftView() {
	}

	/** View ID defined in plugin.xml */
	public static final String ID = "org.csstudio.shift.ui.CreateShift";


	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FormLayout());
		shiftWidget = new ShiftWidget(parent, SWT.NONE, true, true);
		final FormData fd_shiftWidget = new FormData();
		fd_shiftWidget.top = new FormAttachment(0, 1);
		fd_shiftWidget.left = new FormAttachment(0, 1);
		fd_shiftWidget.bottom = new FormAttachment(100, -1);
		fd_shiftWidget.right = new FormAttachment(100, -1);
		shiftWidget.setLayoutData(fd_shiftWidget);
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
