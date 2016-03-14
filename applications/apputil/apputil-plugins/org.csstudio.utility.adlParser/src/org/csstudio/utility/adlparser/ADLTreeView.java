package org.csstudio.utility.adlparser;

import java.io.File;


import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.ParserADL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class ADLTreeView extends ViewPart {
    public static final String ID = "org.csstudio.utility.adlParser.ADLTreeView";
    private TreeViewer treeViewer;
    private ISelectionListener pageSelectionListener;
    ADLWidget adlRootWidget;

    public ADLTreeView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        treeViewer = new TreeViewer(composite);
        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.setContentProvider(new ADLTreeContentProvider());
        treeViewer.setLabelProvider(new ADLTreeLabelProvider());
        treeViewer.setInput(new ADLWidget("default", null, 0));

        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                // TODO Auto-generated method stub

            }
        });
        hookPageSelection();

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }
    private void hookPageSelection() {
        pageSelectionListener = new ISelectionListener() {
            @Override
            public void selectionChanged(IWorkbenchPart part,
                    ISelection selection) {
                pageSelectionChanged(part, selection);
            }
        };
        getSite().getPage().addPostSelectionListener(pageSelectionListener);
    }

    protected void pageSelectionChanged( IWorkbenchPart part, ISelection selection){
        if (part == this){
            return;
        }
        if (!(selection instanceof IStructuredSelection)){
            return;
        }
        IStructuredSelection strucSelection = (IStructuredSelection)selection;
        if (strucSelection.size() > 1){
            return;
        }

        String selectedFileName = new String(Platform.getLocation() + (strucSelection.getFirstElement().toString()).substring(1));

        if (selectedFileName.endsWith(".adl")) {

            treeViewer.setInput(this.adlRootWidget = ParserADL.getNextElement(new File(selectedFileName)));
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        if (pageSelectionListener != null){
            getSite().getPage().removePostSelectionListener(pageSelectionListener);
        }
    }

}
