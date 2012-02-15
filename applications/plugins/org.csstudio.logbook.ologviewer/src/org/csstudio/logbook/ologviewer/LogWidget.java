package org.csstudio.logbook.ologviewer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.msu.nscl.olog.api.Log;

public class LogWidget extends Composite {
	// Model
	private Log log;
	
	private Text textLog;
	private Label lblTime;
	private Label lblOwner;

	public LogWidget(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new GridLayout(2, false));
		
		lblTime = new Label(this, SWT.WRAP);
		lblTime.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblTime.setText("New Label");
		
		lblOwner = new Label(this, SWT.NONE);
		lblOwner.setAlignment(SWT.RIGHT);
		lblOwner.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOwner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblOwner.setText("New Label");
		
		textLog = new Text(this, SWT.WRAP | SWT.DOUBLE_BUFFERED);
		textLog.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_textLog = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 3);
		gd_textLog.verticalIndent = 1;
		textLog.setLayoutData(gd_textLog);
	}

	public void setLog(Log log) {
		this.log = log;
		refresh();
	}

	private void refresh() {
		lblTime.setText(log.getCreatedDate().toString());
		lblOwner.setText(log.getOwner());
		textLog.setText(log.getDescription());
	}	

}
