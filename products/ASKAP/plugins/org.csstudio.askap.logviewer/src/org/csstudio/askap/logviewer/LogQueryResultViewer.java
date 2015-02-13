package org.csstudio.askap.logviewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.logviewer.ui.LogMessageTable;
import org.csstudio.askap.logviewer.util.LogQueryDataModel;
import org.csstudio.askap.utility.AskapEditorInput;
import org.csstudio.askap.utility.icemanager.LogObject.LogQueryObject;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

public class LogQueryResultViewer extends EditorPart {

	private static Logger logger = Logger.getLogger(LogQueryResultViewer.class.getName());
	private static final String ID = "org.csstudio.askap.logviewer.LogQueryResultViewer";

	private LogMessageTable messageTable;
	private LogQueryDataModel dataModel;

	public LogQueryResultViewer() {
		messageTable = new LogMessageTable();
		dataModel = new LogQueryDataModel(Preferences.getLogQueryAdaptorName(), 
				Preferences.getLogQueryMessagesPerQuery(), 
				Preferences.getMaxMessages());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
        setSite(site);
        setPartName(input.getName());
    	setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(final Composite parent) {
		final Composite page = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 20;
		page.setLayout(gridLayout);

		Control control = messageTable.createLogTable(page, dataModel, false);
		GridData tableGridData = new GridData();
		tableGridData.horizontalAlignment = GridData.FILL;	
		tableGridData.verticalAlignment = GridData.FILL;	
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.grabExcessVerticalSpace = true;
		control.setLayoutData(tableGridData);
		
		Button moreButton = new Button(page, SWT.PUSH);
		moreButton.setText("more");
		
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;	
		gd.grabExcessHorizontalSpace = true;
		moreButton.setLayoutData(gd);
		
		moreButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				LogQueryObject query = dataModel.getLastQuery();
				if (query==null) {
					logger.log(Level.INFO, "No query yet");
					return;
				}
				query.startIndex = dataModel.getSize();
				try {
					dataModel.getLogMessage(query);
					messageTable.updateTable();					
				} catch (Exception ex) {
					logger.log(Level.WARNING, "Could not query log server for messages", ex);
		            ExceptionDetailsErrorDialog.openError(parent.getShell(),
		                    "ERROR",
		                    "Could not query log server for messages",
		                    ex);
				} 
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub	
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static Object openLogResultViewer() {
		try {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			final IWorkbenchWindow window = workbench
					.getActiveWorkbenchWindow();
			final IWorkbenchPage page = window.getActivePage();

			return page.openEditor(new AskapEditorInput(
					"Log Query Result Viewer"), ID);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot create LogQueryResultViewer", ex);
		}
		return null;
	}

	public LogQueryDataModel getDataModel() {
		return dataModel;
	}

	public void updateTable() {
		messageTable.updateTable();
	}

}
