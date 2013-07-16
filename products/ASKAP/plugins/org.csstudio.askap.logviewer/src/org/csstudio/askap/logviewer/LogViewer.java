package org.csstudio.askap.logviewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.logviewer.ui.LogMessageTable;
import org.csstudio.askap.logviewer.util.LogDataModel;
import org.csstudio.askap.utility.AskapEditorInput;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

public class LogViewer extends EditorPart {
	private static Logger logger = Logger.getLogger(LogViewer.class.getName());
	public static final String ID = "org.csstudio.askap.logviewer.logviewer";
	
	private LogMessageTable messageTable;
	private LogDataModel dataModel = null;


	public LogViewer() {
		messageTable = new LogMessageTable();
		dataModel = new LogDataModel(Preferences.getLogMessageTopicName(), 
				Preferences.getLogQueryAdaptorName(), 
				Preferences.getLogQueryMessagesPerQuery(), 
				Preferences.getMaxMessages(), 
				Preferences.getLogSubscriberEndPointName());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

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
	public void createPartControl(Composite parent) {
		
		messageTable.createLogTable(parent, dataModel, true);
		
		try {
			dataModel.start();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not subscribe to log messages", e);
			
            ExceptionDetailsErrorDialog.openError(parent.getShell(),
                    "ERROR",
                    "Could not subscribe to log message",
                    e);
		}
		
        getSite().getPage().addPartListener(new IPartListener2() {
        	
            private boolean isThisEditor(final IWorkbenchPartReference part) {
                return (part.getPart(false) instanceof LogViewer);
            }

			
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				if (isThisEditor(partRef))
					messageTable.startUpdates();
			}
			
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				if (isThisEditor(partRef))
					messageTable.stop();
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				if (isThisEditor(partRef))
					messageTable.haltUpdates();
			}
			

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub			
			}
			
			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	
	public static LogViewer openLogViewer() {
        try {
        	final IWorkbench workbench = PlatformUI.getWorkbench();
        	final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        	final IWorkbenchPage page = window.getActivePage();
        	
            return (LogViewer) page.openEditor(new AskapEditorInput("Realtime Log Viewer"), ID);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot create LogViewer", ex);
		}
        
        return null;		
	}

}
