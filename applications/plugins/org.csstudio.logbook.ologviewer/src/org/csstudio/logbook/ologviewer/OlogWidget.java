package org.csstudio.logbook.ologviewer;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.msu.nscl.olog.api.Log;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.ScrolledComposite;

public class OlogWidget extends Composite {

	// Model
	private Collection<Log> logs;
	
	ScrolledComposite scrolledComposite;
	Composite composite;
	
	
	public OlogWidget(final Composite parent, int style) {
		super(parent, SWT.NONE);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		createLogWidgets();
	}
	
	private void createLogWidgets(){
		if (logs != null) {
			for (Log log : logs) {
				LogWidget logWidget = new LogWidget(composite, SWT.NONE);
				logWidget.setLog(log);
				logWidget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			}
		}
//		composite.setSize(composite.computeSize(composite.getShell().getSize().x, SWT.DEFAULT));
		System.out.println(composite.getSize().toString() +"   "+composite.getShell().getSize().toString());
		scrolledComposite.setMinSize(composite.computeSize(composite.getShell().getSize().x, SWT.DEFAULT));
	}
	

	public Collection<Log> getLogs() {
		return logs;
	}

	public void setLogs(Collection<Log> logs) {
		this.logs = logs;
		createLogWidgets();
	}

}
