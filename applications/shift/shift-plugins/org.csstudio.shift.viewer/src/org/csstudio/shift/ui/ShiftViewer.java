package org.csstudio.shift.ui;

import gov.bnl.shiftClient.Shift;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.csstudio.shift.ShiftBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
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


public class ShiftViewer extends EditorPart {

    public static final String ID = "org.csstudio.shift.ui.ShiftViewer";
    private ShiftWidget shiftWidget;
    private ISelectionListener selectionListener;

    private static ShiftViewer editor;

    /**
     * 
     */
    public ShiftViewer() {
    }

    public static ShiftViewer createInstance() {
	    return createInstance(new ShiftViewerModel(null));
    }

    public static ShiftViewer createInstance(final IEditorInput input) {
        try {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            final List<IEditorReference> editors = Arrays.asList(page.getEditorReferences());
            for (IEditorReference iEditorReference : editors) {
            if (iEditorReference.getId().equals(ID)) {
                editor = (ShiftViewer) iEditorReference.getEditor(true);
                editor.setInput(input);
                page.activate(editor);
                return editor;
            }
            }
            editor = (ShiftViewer) page.openEditor(input, ID);
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
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        setSite(site);
        setPartName("Shift");
        setInput(input);
        final ISelectionService ss = getSite().getWorkbenchWindow().getSelectionService();
        selectionListener = new ISelectionListener() {

            @Override
            public void selectionChanged(final IWorkbenchPart part, final  ISelection selection) {
                if (shiftWidget == null || shiftWidget.isDisposed()) {
                    return;
                }
                if (selection instanceof IStructuredSelection) {
                    final Object first = ((IStructuredSelection) selection).getFirstElement();
                    if (first instanceof Shift) {
                        shiftWidget.setShift((Shift) first);
                    } else {
                        try {
                            shiftWidget.setShift(ShiftBuilder.withType("").build());
                        } catch (IOException e) {
                        }
                    }
                }
            }
        };
        ss.addSelectionListener(org.csstudio.shift.ui.ShiftTableView.ID, selectionListener);
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
    public void createPartControl(final Composite parent) {
        shiftWidget = new ShiftWidget(parent, SWT.NONE, false, false);
        final Shift shift = ((ShiftViewerModel) getEditorInput()).getShift();
        if (shift != null) {
            shiftWidget.setShift(((ShiftViewerModel) getEditorInput()).getShift());
        }
        shiftWidget.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(final DisposeEvent arg0) {
                final ISelectionService ss = getSite().getWorkbenchWindow().getSelectionService();
                ss.removeSelectionListener(org.csstudio.shift.ui.ShiftTableView.ID, selectionListener);
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
