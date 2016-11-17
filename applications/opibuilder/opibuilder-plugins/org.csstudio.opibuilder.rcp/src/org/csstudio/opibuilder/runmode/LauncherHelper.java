package org.csstudio.opibuilder.runmode;

import java.util.Iterator;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

/**
 *
 * <code>LauncherHelper</code> is a utility class that provides common methods needed for launching the OPI from the
 * resource navigator.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class LauncherHelper {

    private LauncherHelper() {}

    /**
     * Check the resources that are selected in the navigator and find the one that matches the given path. The returned
     * path is always the path within the workspace whereas the parameter is an absolute system path.
     *
     * @param path the path for which we need a workspace resource path
     * @return the workspace path if a match in the workspace was found or the same path if a match was not found
     */
    public static IPath systemPathToWorkspacePath(IPath path) {
        //path is an absolute file location, which needs to be transformed into a workspace resource
        //This method was triggered from the resource navigator, therefore the current selection should contain the
        //resource that matches this path

        ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
        ISelection selection = service.getSelection();

        if (selection instanceof IStructuredSelection) {
            Iterator<?> elements = ((IStructuredSelection)selection).iterator();

            //There might be multiple resources selected in the navigator. We need to find the one item that has the
            //same path as provided as a parameter.

            while(elements.hasNext()) {
                Object e = elements.next();
                //compare the resource location with the path. According the FileEditorInput, the path is the location
                //of the selected file
                if (e instanceof IResource && ((IResource)e).getLocation().equals(path)) {
                    //TODO
                    //There is a problem if the user opens more paths at the same time. If all opened paths point to the
                    //the same physical location, this code below will always result to the first selected item. In
                    //reality it is a very unlikely event.
                    return ((IResource)e).getFullPath();
                }
            }
        }
        OPIBuilderPlugin.getLogger().info(NLS.bind("A workspace match for {0} could not be found.", path));
        return path;
    }
}
