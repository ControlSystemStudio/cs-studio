package org.csstudio.opibuilder.runmode;

import java.util.HashSet;
import java.util.Set;

import org.csstudio.opibuilder.actions.RefreshOPIAction;
import org.csstudio.opibuilder.datadefinition.NotImplementedException;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;


public class OPIShell implements IOPIRuntime {

    // Cache of open OPI shells.
    private static final Set<OPIShell> openShells = new HashSet<OPIShell>();

    private Shell shell;
    private IPath path;
    private MacrosInput macrosInput;
    private final ActionRegistry actionRegistry;
    private DisplayModel displayModel;

    // Private constructor means you can't open an OPIShell without adding
    // it to the cache.
    private OPIShell(Display display, IPath path, MacrosInput macrosInput) {
        this.path = path;
        this.macrosInput = macrosInput;
        this.shell = new Shell(display);
        this.displayModel = new DisplayModel(path);
        this.actionRegistry = new ActionRegistry();

        final GraphicalViewer viewer = new GraphicalViewerImpl();
        shell.setLayout(new FillLayout());

        try {
            XMLUtil.fillDisplayModelFromInputStream(ResourceUtil.pathToInputStream(path), displayModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(macrosInput != null) {
            macrosInput = macrosInput.getCopy();
            macrosInput.getMacrosMap().putAll(displayModel.getMacrosInput().getMacrosMap());
            displayModel.setPropertyValue(AbstractContainerModel.PROP_MACROS, macrosInput);
        }

        viewer.createControl(shell);
        viewer.setEditPartFactory(new WidgetEditPartFactory(ExecutionMode.RUN_MODE));
        viewer.setRootEditPart(new ScalableFreeformRootEditPart() {
            @Override
            public DragTracker getDragTracker(Request req) {
                return new DragEditPartsTracker(this);
            }
            @Override
            public boolean isSelectable() {
                return false;
            }
        });

        EditDomain editDomain = new EditDomain() {
            @Override
            public void loadDefaultTool() {
                setActiveTool(new RuntimePatchedSelectionTool());
            }
        };
        editDomain.addViewer(viewer);

        actionRegistry.registerAction(new RefreshOPIAction(this));
        SingleSourceHelper.registerRCPRuntimeActions(actionRegistry, this);
        OPIRunnerContextMenuProvider contextMenuProvider = new OPIRunnerContextMenuProvider(viewer, this);
        getSite().registerContextMenu(contextMenuProvider, viewer);
        viewer.setContextMenu(contextMenuProvider);

        viewer.setContents(displayModel);
        displayModel.setViewer(viewer);

        // Set title
        if (displayModel.getName() != null && displayModel.getName().trim().length() > 0) {
            shell.setText(displayModel.getName());
        } else { // If the name doesn't exist, use the OPI path
            shell.setText(path.toString());
        }

        shell.addShellListener(new ShellListener() {
            private boolean firstRun = true;
            public void shellIconified(ShellEvent e) {}
            public void shellDeiconified(ShellEvent e) {}
            public void shellDeactivated(ShellEvent e) {}
            public void shellClosed(ShellEvent e) {
                // Remove this shell from the cache.
                openShells.remove(OPIShell.this);
            }
            public void shellActivated(ShellEvent e) {
                if (firstRun) {
                    // Resize the shell after it's open, so we can take into account different window borders.
                    // Do this only the first time it's activated.
                    int frameX = shell.getSize().x - shell.getClientArea().width;
                    int frameY = shell.getSize().y - shell.getClientArea().height;
                    shell.setSize(displayModel.getSize().width + frameX, displayModel.getSize().height + frameY);
                    shell.setFocus();
                    firstRun = false;
                }
            }
        });
        shell.pack();
        /*
         * Don't open the Shell here, as it causes SWT to think the window is on top when it really isn't.
         * Wait until the window is open, then call shell.setFocus() in the activated listener.
         *
         * Make some attempt at sizing the shell, sometimes a shell is not given focus and the shellActivated
         * listener callback doesn't resize the window. It's better to have something a little to large as the
         * default. Related to Eclipse bug 96700.
         */
        int windowBorderX = 30;
        int windowBorderY = 30;
        shell.setVisible(true);
        shell.setSize(displayModel.getSize().width + windowBorderX, displayModel.getSize().height + windowBorderY);
    }
    
    public MacrosInput getMacrosInput() {
        return macrosInput;
    }
    
    public IPath getPath() {
        return path;
    }
    
    public void raiseToTop() {
        shell.forceFocus();
        shell.forceActive();
        shell.setFocus();
        shell.setActive();
    }
    
    @Override
    public  boolean equals(Object o) {
        if (o instanceof OPIShell) {
            OPIShell opiShell = (OPIShell) o;
        return opiShell.getMacrosInput().equals(this.getMacrosInput())
                && opiShell.getPath().equals(this.path);
        } else {
            return false;
        }
    }

    /*
     * This is the only way to create an OPIShell
     */
    public static void openOPIShell(final IPath path, final MacrosInput macrosInput) {
        try {
            boolean alreadyOpen = false;
            for (OPIShell opiShell : openShells) {
                if (opiShell.getPath().equals(path) && opiShell.getMacrosInput().equals(macrosInput)) {
                    opiShell.raiseToTop();
                    alreadyOpen = true;
                }
            }
            if (!alreadyOpen) {
                OPIShell os = new OPIShell(Display.getCurrent(), path, macrosInput);
                openShells.add(os);
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    /********************************************
     * Partial implementation of IOPIRuntime
     ********************************************/
    @Override
    public void addPropertyListener(IPropertyListener listener) {
        throw new NotImplementedException();
    }

    @Override
    public void createPartControl(Composite parent) {
        throw new NotImplementedException();
    }

    @Override
    public void dispose() {
        shell.dispose();
        actionRegistry.dispose();
    }

    @Override
    public IWorkbenchPartSite getSite() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
    }

    @Override
    public String getTitle() {
        return shell.getText();
    }

    @Override
    public Image getTitleImage() {
        throw new NotImplementedException();
    }

    @Override
    public String getTitleToolTip() {
        return shell.getToolTipText();
    }

    @Override
    public void removePropertyListener(IPropertyListener listener) {
        throw new NotImplementedException();
    }

    @Override
    public void setFocus() {
        throw new NotImplementedException();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if(adapter == ActionRegistry.class)
            return this.actionRegistry;

        return null;
    }

    @Override
    public void setWorkbenchPartName(String name) {
        throw new NotImplementedException();
    }

    @Override
    public void setOPIInput(IEditorInput input) throws PartInitException {
        IPath path = ((IFileEditorInput) input).getFile().getFullPath();
        MacrosInput macrosInput = displayModel.getMacrosInput();
        GraphicalViewer viewer = displayModel.getViewer();

        displayModel = new DisplayModel(path);
        try {
            XMLUtil.fillDisplayModelFromInputStream(ResourceUtil.pathToInputStream(path), displayModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(macrosInput != null) {
            macrosInput = macrosInput.getCopy();
            macrosInput.getMacrosMap().putAll(displayModel.getMacrosInput().getMacrosMap());
            displayModel.setPropertyValue(AbstractContainerModel.PROP_MACROS, macrosInput);
        }

        viewer.setContents(displayModel);
        displayModel.setViewer(viewer);
    }

    @Override
    public IEditorInput getOPIInput() {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(displayModel.getOpiFilePath());
        return new FileEditorInput(file);
    }

    @Override
    public DisplayModel getDisplayModel() {
        return displayModel;
    }

    /**
     *  Getter for the Shell associated with this OPIShell
     */
    public Shell getShell() {
    	return this.shell;
    }

    /** Search the cache of open OPIShells to find a match for the
     *  input Shell object.
     *
     * 	Return associated OPIShell or Null if none found
     */
    public static OPIShell getOPIShellForShell(final Shell target) {
    	OPIShell foundShell = null;
    	if (target != null) {
    		for (OPIShell os : OPIShell.openShells) {
    			if (os.getShell() == target) {
    				foundShell = os;
    				break;
    			}
    		}
    	}
    	return foundShell;
    }

}
