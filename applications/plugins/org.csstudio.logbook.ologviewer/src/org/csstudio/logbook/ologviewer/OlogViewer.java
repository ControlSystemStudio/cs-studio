package org.csstudio.logbook.ologviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import edu.msu.nscl.olog.api.TestLogs;

public class OlogViewer extends ViewPart {

	public OlogViewer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		
		Label lblSearch = new Label(parent, SWT.NONE);
		lblSearch.setText("Search:");
		FormData fd_lblSearch = new FormData();
		lblSearch.setLayoutData(fd_lblSearch);
		
		Combo combo = new Combo(parent, SWT.NONE);
		fd_lblSearch.top = new FormAttachment(combo, 3, SWT.TOP);
		fd_lblSearch.right = new FormAttachment(combo, -6);
		
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -10);
		fd_combo.left = new FormAttachment(0, 63);
		fd_combo.top = new FormAttachment(0, 10);
		combo.setLayoutData(fd_combo);
		// TODO Auto-generated method stub
		
		OlogWidget ologWidget = new OlogWidget(parent, SWT.None);
		FormData fd_ologWidget = new FormData();
		fd_ologWidget.top = new FormAttachment(lblSearch, 10);
		fd_ologWidget.right = new FormAttachment(100, -10);
		fd_ologWidget.left = new FormAttachment(0, 10);
		fd_ologWidget.bottom = new FormAttachment(100, -10);
		ologWidget.setLayoutData(fd_ologWidget);
		ologWidget.setLogs(TestLogs.testLogs());

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
