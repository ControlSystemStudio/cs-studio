package org.csstudio.ui.menu.test;


import org.csstudio.csdata.ProcessVariable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ProcessVariablePopupTestView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.ui.menu.test.ProcessVariablePopupTestView";

    private TableViewer pvViewer;
    private Table toPvTable;

    /*
     * The content provider class is responsible for
     * providing objects to the view. It can wrap
     * existing objects in adapters or simply return
     * objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore
     * it and always show the same content
     * (like Task List, for example).
     */

    class ViewContentProvider implements IStructuredContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }
        public void dispose() {
        }
        public Object[] getElements(Object parent) {
            return new ProcessVariable[] { new ProcessVariable("sim://gaussianWaveform()"),
                    new ProcessVariable("sim://noise"),
                    new ProcessVariable("sim://ramp(1,10,1,0.1)") };
        }
    }
    class ToPvContentProvider implements IStructuredContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }
        public void dispose() {
        }
        public Object[] getElements(Object parent) {
            return new CustomProcessVariable[] { new CustomProcessVariable("sim://gaussianWaveform()"),
                    new CustomProcessVariable("sim://noise"),
                    new CustomProcessVariable("sim://ramp(1,10,1,0.1)") };
        }
    }
    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object obj, int index) {
            return getText(obj);
        }
        public Image getColumnImage(Object obj, int index) {
            return null;
        }
        public Image getImage(Object obj) {
            return PlatformUI.getWorkbench().
                    getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }
    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public ProcessVariablePopupTestView() {
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        Label lblProcessVariables = new Label(parent, SWT.NONE);
        lblProcessVariables.setText("ProcessVariable:");

        Label lblAdaptableToProcessvariable = new Label(parent, SWT.NONE);
        lblAdaptableToProcessvariable.setText("Adaptable to ProcessVariable:");
        pvViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        Table pvTable = pvViewer.getTable();
        pvTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        pvViewer.setContentProvider(new ViewContentProvider());
        pvViewer.setLabelProvider(new ViewLabelProvider());
        pvViewer.setSorter(new NameSorter());
        pvViewer.setInput(getViewSite());

        TableViewer toPvViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        toPvTable = toPvViewer.getTable();
        toPvTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        toPvViewer.setContentProvider(new ToPvContentProvider());
        toPvViewer.setLabelProvider(new ViewLabelProvider());
        toPvViewer.setSorter(new NameSorter());
        toPvViewer.setInput(getViewSite());
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                // After a right click command changes the active view,
                // another right click does not change it anymore (bug in Eclipse?)
                // so we forcefully change it before the menu is about to show.
                try {
                    getSite().getPage().showView(ID);
                } catch(Exception ed) {

                }
                ProcessVariablePopupTestView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(toPvViewer.getControl());
        toPvViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, toPvViewer);

        hookContextMenu();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                // After a right click command changes the active view,
                // another right click does not change it anymore (bug in Eclipse?)
                // so we forcefully change it before the menu is about to show.
                try {
                    getSite().getPage().showView(ID);
                } catch(Exception ed) {

                }
                ProcessVariablePopupTestView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(pvViewer.getControl());
        pvViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, pvViewer);
    }

    private void fillContextMenu(IMenuManager manager) {
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        pvViewer.getControl().setFocus();
    }
}