package org.csstudio.startuphelper.module;

import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.startup.module.ProjectExtPoint;
import org.csstudio.startuphelper.Messages;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/** DefaultProject opens and closes the application's default
 *  project. The name of the project is localized.
 * 
 *  @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *  @author Kay Kasemir
 */
public class DefaultProject implements ProjectExtPoint
{
    /** {@inheritDoc} */
    public Object openProjects(Display display, IApplicationContext context,
            Map<String, Object> parameters)
    {
        // Must call something from the CSS UI plugin.
        // Exact mechanism unclear, but when NOT doing this,
        // the initial Navigator instance won't show
        // the 'CSS' project that we're about to create/open?!
        CentralLogger.getInstance().getLogger(this).debug("CSS UI plugin: " + //$NON-NLS-1$
                CSSPlatformUiPlugin.getDefault().getPluginId());

        // Assert that there is an open "CSS" project.
        // Without that, an existing 'CSS' might show up,
        // but a 'new Folder' action would run into
        // 'project not open' error...
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
                Messages.DefaultProjectName);
        // Assert that it exists...
        if (!project.exists())
        {
            try
            {
                project.create(new NullProgressMonitor());
            }
            catch (CoreException ex)
            {
                MessageDialog.openError(null, Messages.Error,
                        NLS.bind(Messages.CreateProjectErrorFmt,
                                 project.getName(), ex.getMessage()));
                // Give up, quit.
                return null;
            }
        }
        // .. and open it
        try
        {
            project.open(new NullProgressMonitor());
            // put project into parameters map as requested by API to make it
            // available to other extension points
            parameters.put(PROJECTS, new IProject[] { project });
            return null;
        }
        catch (CoreException ex)
        {
            MessageDialog.openError(null, Messages.Error,
                    NLS.bind(Messages.OpenProjectErrorFmt,
                            project.getName(), ex.getMessage()));
        }
        return null;
    }

    /** {@inheritDoc} */
    public Object closeProjects(Display display, IApplicationContext context,
            Map<String, Object> parameters) throws Exception
    {
        final IProject projects[] = (IProject[]) parameters.get(PROJECTS);
        if (projects != null)
        {
            for (IProject project : projects)
            {
                try
                {
                    project.close(new NullProgressMonitor());
                }
                catch (CoreException ex)
                {
                    MessageDialog.openError(null, Messages.Error,
                            NLS.bind(Messages.CloseProjectErrorFmt,
                                    project.getName(), ex.getMessage()));
                }
            }
        }
        return null;
    }
}
