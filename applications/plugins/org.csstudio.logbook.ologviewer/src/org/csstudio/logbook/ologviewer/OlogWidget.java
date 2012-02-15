package org.csstudio.logbook.ologviewer;

import java.util.Collection;

import org.csstudio.logbook.ologviewer.OlogQuery.Result;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.msu.nscl.olog.api.Log;

public class OlogWidget extends Composite {

	// Model
	private OlogQuery ologQuery;

	public OlogQuery getOlogQuery() {
		return ologQuery;
	}

	public void setOlogQuery(OlogQuery ologQuery) {
		// If new query is the same, don't change -- you would re-trigger the
		// query for nothing
		if (getOlogQuery() != null && getOlogQuery().equals(ologQuery))
			return;
		if (getOlogQuery() == null && ologQuery == null)
			return;

		OlogQuery oldValue = getOlogQuery();
		if (oldValue != null) {
			oldValue.removeOlogQueryListener(queryListener);
		}

		queryCleared();

		if (ologQuery != null) {
			ologQuery.execute(queryListener);
		}

		this.ologQuery = ologQuery;
	}

	private void queryCleared() {
		errorBar.setException(null);
		setLogs(null);
	}

	OlogQueryListener queryListener = new OlogQueryListener() {

		@Override
		public void queryExecuted(final Result result) {
			SWTUtil.swtThread().execute(new Runnable() {
				@Override
				public void run() {
					Exception e = result.exception;
					errorBar.setException(e);
					if (e == null) {
						setLogs(result.logs);
					}
				}
			});
		}
	};

	private Collection<Log> logs;

	ScrolledComposite scrolledComposite;
	Composite composite;
	private ErrorBar errorBar;

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
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);		

		errorBar = new ErrorBar(scrolledComposite, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		scrolledComposite.setContent(errorBar);
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));


		createLogWidgets();
	}

	private void createLogWidgets() {
		if (logs != null) {
			for (Log log : logs) {
				LogWidget logWidget = new LogWidget(composite, SWT.NONE);
				logWidget.setLog(log);
				logWidget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						true, false, 1, 1));
			}
		} else {
			Control[] children = composite.getChildren();
			for (Control control : children) {
				control.dispose();
			}
		}
		scrolledComposite.setMinSize(composite.computeSize(10, SWT.DEFAULT));
	}

	public Collection<Log> getLogs() {
		return logs;
	}

	private void setLogs(Collection<Log> logs) {
		this.logs = logs;
		createLogWidgets();
	}

}
