/**
 * 
 */
package org.csstudio.logbook.ui;

import org.csstudio.logbook.LogEntry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * @author shroffk
 * 
 */
public class LogViewer extends EditorPart {

    public static final String ID = "org.csstudio.logbook.ui.LogViewer";
    private LogEntryWidget logEntryWidget;
    private ISelectionListener selectionListener;

    /**
     * 
     */
    public LogViewer() {
    }

    public static LogViewer createInstance() {
	return createInstance(new LogViewerModel(null));
    }

    public static LogViewer createInstance(final IEditorInput input) {
	final LogViewer editor;
	try {
	    final IWorkbench workbench = PlatformUI.getWorkbench();
	    final IWorkbenchWindow window = workbench
		    .getActiveWorkbenchWindow();
	    final IWorkbenchPage page = window.getActivePage();
	    editor = (LogViewer) page.openEditor(input, ID);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    return null;
	}
	return editor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
     * IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
     * org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input)
	    throws PartInitException {
	setSite(site);
	setPartName("Log Entry");
	setInput(input);
	ISelectionService ss = getSite().getWorkbenchWindow()
		.getSelectionService();
	selectionListener = new ISelectionListener() {

	    @Override
	    public void selectionChanged(IWorkbenchPart part,
		    ISelection selection) {
	    	if(logEntryWidget==null || logEntryWidget.isDisposed()) {
	    		return;
	    	}
		if (selection instanceof IStructuredSelection) {
		    Object first = ((IStructuredSelection) selection)
			    .getFirstElement();
		    if (first instanceof LogEntry) {
		    	logEntryWidget.setLogEntry((LogEntry) first);
		    }
		}
	    }
	};
	ss.addSelectionListener(org.csstudio.logbook.ui.LogTableView.ID, selectionListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
	public void createPartControl(Composite parent) {
		logEntryWidget = new LogEntryWidget(parent, SWT.NONE, false, false);
		logEntryWidget.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				ISelectionService ss = getSite().getWorkbenchWindow()
						.getSelectionService();
				ss.removeSelectionListener(
						org.csstudio.logbook.ui.LogTableView.ID,
						selectionListener);
			}
		});
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
