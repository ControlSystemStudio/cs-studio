package org.csstudio.opibuilder.runmode;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
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
    // macrosInput should not be null.  If there are no macros it should
    // be an empty MacrosInput object.
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
        this.displayModel.setOpiRuntime(this);
        this.actionRegistry = new ActionRegistry();

        final GraphicalViewer viewer = new GraphicalViewerImpl();
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

        try {
            displayModel = createDisplayModel(path, macrosInput, viewer);
            setTitle();

            shell.setLayout(new FillLayout());
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
                        resizeToContents();
                        shell.setFocus();
                        firstRun = false;
                    }
                }
            });
            shell.pack();
            if (!displayModel.getLocation().equals(DisplayModel.NULL_LOCATION)) {
                shell.setLocation(displayModel.getLocation().getSWTPoint());
            }
            /*
             * Don't open the Shell here, as it causes SWT to think the window is on top when it really isn't.
             * Wait until the window is open, then call shell.setFocus() in the activated listener.
             *
             * Make some attempt at sizing the shell, sometimes a shell is not given focus and the shellActivated
             * listener callback doesn't resize the window. It's better to have something a little too large as the
             * default. Related to Eclipse bug 96700.
             */
            int windowBorderX = 30;
            int windowBorderY = 30;
            shell.setSize(displayModel.getSize().width + windowBorderX, displayModel.getSize().height + windowBorderY);
            shell.setVisible(true);
        } catch (Exception e) {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to create new OPIShell.", e);
        }
    }

    private DisplayModel createDisplayModel(IPath path, MacrosInput macrosInput, GraphicalViewer viewer)
            throws Exception {
        displayModel = new DisplayModel(path);
        XMLUtil.fillDisplayModelFromInputStream(ResourceUtil.pathToInputStream(path), displayModel);
        if(macrosInput != null) {
            macrosInput = macrosInput.getCopy();
            macrosInput.getMacrosMap().putAll(displayModel.getMacrosInput().getMacrosMap());
            displayModel.setPropertyValue(AbstractContainerModel.PROP_MACROS, macrosInput);
        }

        viewer.setContents(displayModel);
        displayModel.setViewer(viewer);
        displayModel.setOpiRuntime(this);
        return displayModel;
    }

    private void setTitle() {
        if (displayModel.getName() != null && displayModel.getName().trim().length() > 0) {
            shell.setText(displayModel.getName());
        } else { // If the name doesn't exist, use the OPI path
            shell.setText(path.toString());
        }
    }

    private void resizeToContents() {
        int frameX = shell.getSize().x - shell.getClientArea().width;
        int frameY = shell.getSize().y - shell.getClientArea().height;
        shell.setSize(displayModel.getSize().width + frameX, displayModel.getSize().height + frameY);
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
    public static void openOPIShell(IPath path, MacrosInput macrosInput) {
        if (macrosInput == null) {
            macrosInput = new MacrosInput(new LinkedHashMap<String, String>(), false);
        }
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

    /**
     *  Getter for the Shell associated with this OPIShell
     */
    public Shell getShell() {
        return this.shell;
    }

    /** Search the cache of open OPIShells to find a match for the
     *  input Shell object.
     *
     *     Return associated OPIShell or Null if none found
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
        try {
            IPath path = null;
            if (input instanceof IFileEditorInput) {
                path = ((IFileEditorInput) input).getFile().getFullPath();
            } else if (input instanceof RunnerInput) {
                path = ((RunnerInput) input).getPath();
            }
            MacrosInput macrosInput = displayModel.getMacrosInput();
            GraphicalViewer viewer = displayModel.getViewer();
            displayModel = createDisplayModel(path, macrosInput, viewer);
            setTitle();
            resizeToContents();
        } catch (Exception e) {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to replace OPIShell contents.", e);
        }
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

    @Override
    public int hashCode() {
        return Objects.hash(OPIShell.class, macrosInput, path);
    }
}
