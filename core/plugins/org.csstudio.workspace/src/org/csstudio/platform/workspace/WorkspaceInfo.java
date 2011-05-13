package org.csstudio.platform.workspace;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.service.prefs.Preferences;

/** Information about a workspace selection: Current, history, ...
 *  <p>
 *  <b>Code is based upon
 *  <code>org.eclipse.ui.internal.ide.ChooseWorkspaceData</code>.
 *  </p>
 *  @author Alexander Will
 *  @author Kay Kasemir
 */
public class WorkspaceInfo
{
    /** Qualifier used as preference store ID */
    private static final String PREF_QUALIFIER = WorkspaceInfo.class.getName();

    /** Preference ID */
    private static final String SHOW_DIALOG = "show_dialog"; //$NON-NLS-1$

    /** Preference ID */
    private static final String RECENT_WORKSPACES = "recent_workspaces"; //$NON-NLS-1$

    /** Separator used to store list of workspace entries in one string */
    private static final String WORKSPACE_SEPARATOR = ","; //$NON-NLS-1$

    /** Number of recent workspaces that we remember */
    private static final int RECENT_WORKSPACE_MAX = 10;
    
    /** Path separator '/' */
    private static final String SLASH = "/";  //$NON-NLS-1$

    /** Current workspace */
    private Location current_workspace;

    /** Show the select-workspace dialog? */
    private boolean show_dialog;
    
    /** List of recent workspaces, most recent one on top */
    final private ArrayList<String> recent_workspaces = new ArrayList<String>();

    /** Constructor
     *  @param initial_default The initially selected workspace
     *  @param select_previous_workspace If <code>true</code>, the previously
     *         selected workspace (if there is one) will be suggested.
     *         If <code>false</code>, the <code>initial_default</code> will
     *         be used as the initial selection.
     */
    public WorkspaceInfo(final URL initial_default, final boolean select_previous_workspace)
    {
        current_workspace = Platform.getInstanceLocation();
        readPersistedData();
        // Do we have info about recent workspaces, and we're asked to
        // use the previous selection as the default?
        if (select_previous_workspace && recent_workspaces.size() > 0)
            return; // OK, recent_workspaces.get(0) is the one we want
        
        // No previous info, or we're asked to use initial_default in any case:
        if(initial_default != null)
        	setSelectedWorkspace(initial_default.getFile());
        else
        	//if no default workspace provided, use "@usr.home/CSS-Workspaces/Default"
        	setSelectedWorkspace(System.getProperty("user.home") //$NON-NLS-1$
                    + File.separator + "CSS-Workspaces" + File.separator + "Default");
    }

    /** Read previous settings from preference store */
    @SuppressWarnings("nls")
    private void readPersistedData()
    {
    	final Preferences node = new ConfigurationScope().getNode(PREF_QUALIFIER);
    //    final IPreferenceStore store = new ScopedPreferenceStore(
    //            new ConfigurationScope(), PREF_QUALIFIER);
        
        // Get value for show_dialog.
        final String dlg_info = node.get(SHOW_DIALOG, "true");
        // We use a default of true if nothing was specified.
        // Only use false when we specifically find "false" in the store.
        show_dialog = ! "false".equals(dlg_info);

        // Get recent workspaces
        recent_workspaces.clear();
        final String recent = node.get(RECENT_WORKSPACES, "");
        // Decode the workspaces
        final StringTokenizer tokenizer =
            new StringTokenizer(recent, WORKSPACE_SEPARATOR);
        for (int i = 0; tokenizer.hasMoreTokens(); ++i)
            recent_workspaces.add(tokenizer.nextToken());
    }

    /** @return Current workspace (which might differ from the selected one) */
    public Location getCurrentWorkspace()
    {
        return current_workspace;
    }
    
    /** @return Selected workspace, the one that the user would like to have,
     *  which is the first one in the list of recent workspace
     */
    public String getSelectedWorkspace()
    {
        if (recent_workspaces.size() < 1)
            return ""; //$NON-NLS-1$
        return recent_workspaces.get(0);
    }
    
    /** Set the selected workspace, add to list of recent workspaces */
    public void setSelectedWorkspace(String workspace)
    {
        // Add to top of list
        recent_workspaces.add(0, normalize(workspace));
        // Remove duplicates of this workspace down the list
        for (int i=1 /* ! */; i<recent_workspaces.size();i++)
            if (recent_workspaces.get(i).equals(normalize(workspace))) {
                recent_workspaces.remove(i);
                i--;
            }
        // Limit list size
        while (recent_workspaces.size() > RECENT_WORKSPACE_MAX)
            recent_workspaces.remove(recent_workspaces.size() - 1);
    }

    /** @return Normalized version of the given workspace string */
    private String normalize(String workspace)
    {
        workspace = workspace.trim().replace('\\', '/');
        // In case there's no initial '/':
        if (! workspace.startsWith(SLASH))
            workspace = SLASH + workspace;
        // In case we get windows '\' slashes, normalize them to the
        // Unix/OS X slashes that the Platform provides for the current
        // workspace.
        workspace = workspace.replace('\\', '/');
        // The 'current' workspace seems to end in '/',
        // while the user input might or might not.
        // Equalize by chopping trailing '/'.
        if (workspace.endsWith(SLASH))
            workspace = workspace.substring(0, workspace.length() -1);
        return workspace;
    }

    /** @return <code>true</code> if the workspace dialog should be displayed */
    public boolean getShowDialog()
    {
        return show_dialog;
    }

    /** Show workspace dialog be shown again? */
    public void setShowDialog(boolean show)
    {
        show_dialog = show;
    }

    /** @return Number of recent workspaces */
    public int getWorkspaceCount()
    {
        return recent_workspaces.size();
    }

    /** @return Recent workspace with given index */
    public String getWorkspace(final int index)
    {
        return recent_workspaces.get(index);
    }

    /** Write settings to preference store */
    @SuppressWarnings("nls")
    public void writePersistedData()
    {
        final Preferences node =
            new ConfigurationScope().getNode(PREF_QUALIFIER);
    
        node.put(SHOW_DIALOG, Boolean.toString(show_dialog));
        
        // Encode recent workspaces into one string
        final StringBuffer recent = new StringBuffer();
        for (String workspace : recent_workspaces)
        {
            recent.append(workspace);
            recent.append(WORKSPACE_SEPARATOR);
        }
        node.put(RECENT_WORKSPACES, recent.toString());
        
        try
        {
            node.flush();
        }
        catch (Exception ex)
        {
            // If we cannot persist workspace info, we have a problem with
            // accessing the workspace or install location,
            // and logging might not work, either...
            Activator.getInstance().getLog().log(
                    new Status(IStatus.ERROR, Activator.ID,
                            "Cannot persist workspace info: "
                            + ex.getMessage()));
        }
    }

    /** @return Debug info */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("Workspace Info:\n");
        // ACHTUNG(!):
        // Calling current_workspace.getURL() on a location that is not "Set"
        // will set it to the default, and once set, one can no longer change
        // the location.
        // This code only prints the URL when isSet:
        if (current_workspace.isSet())
            buf.append("Current: " + current_workspace.getURL() + "\n");
        else
            buf.append("Current: Not set, default: " + current_workspace.getDefault().toString());
        buf.append("Query for workspace: " + show_dialog + "\n");
        buf.append("Recent Workspaces:\n");
        for (String workspace : recent_workspaces)
            buf.append(workspace + "\n");
        return buf.toString();
    }
}
