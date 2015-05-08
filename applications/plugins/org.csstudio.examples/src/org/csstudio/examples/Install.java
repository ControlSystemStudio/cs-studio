/**
 *
 */
package org.csstudio.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;

/**
 * A command to install examples defined using the sampleset extension point.
 *
 * @author shroffk
 *
 */
public class Install extends AbstractHandler {

    private Map<String, URL> selectedURLS = Collections.emptyMap();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

    final Map<String, URL> urls = new HashMap<String, URL>();
    IConfigurationElement[] config = Platform.getExtensionRegistry()
        .getConfigurationElementsFor(SampleSet.ID);
    if (config.length > 0) {
        for (IConfigurationElement iConfigurationElement : config) {
        try {
            urls.put(
                iConfigurationElement.getAttribute("name"),
                ((SampleSet) iConfigurationElement
                    .createExecutableExtension("sampleset")).getDirectoryURL()); //$NON-NLS-1$
        } catch (InvalidRegistryObjectException | CoreException e) {
            e.printStackTrace();
        }
        }
    }
    // Open Selection dialog for the projects to be installed/reinstalled
    ListSelectionDialog listSelectionDialog = new ListSelectionDialog(
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        urls.keySet(), new ArrayContentProvider(),
        new ColumnLabelProvider(),
        "Select the Examples to be installed.");
    listSelectionDialog.setTitle("Install Examples");
    List<String> existingProjects = new ArrayList<String>();
    for (IProject project : root.getProjects()) {
        if (urls.keySet().contains(project.getName())) {
        existingProjects.add(project.getName());
        }
    }
    listSelectionDialog.setInitialElementSelections(existingProjects);
    listSelectionDialog.setBlockOnOpen(true);
    if (listSelectionDialog.open() != Window.OK) {
        return null;
    } else {
        List<Object> selectedExamples = Arrays.asList(listSelectionDialog.getResult());
            selectedURLS  = urls
                    .entrySet()
                    .stream()
                    .filter(url -> {
                        return selectedExamples.contains(url.getKey());
                    })
                    .collect(
                            Collectors.toMap(url -> url.getKey(),
                                    url -> url.getValue()));
    }
    // check for the projects that will be overwritten and ask for
    // confirmation.
    Set<String> overwrite = new HashSet<String>(selectedURLS.keySet());
    overwrite.retainAll(existingProjects);
    if (!overwrite.isEmpty()) {
        String eol = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer(
            "The following example projects will be reinstalled:" + eol);
        sb.append(eol);
        for (String projectName : overwrite) {
        sb.append(" \u2022 ");
        sb.append(projectName);
        sb.append(eol);
        }
        boolean result = MessageDialog.openConfirm(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Confirm Reinstall", sb.toString());

        if (!result) {
        return Status.CANCEL_STATUS;
        }
    }

    Job job = new Job("Import Examples") {

        @Override
        protected IStatus run(IProgressMonitor monitor) {
        // copy the sample displays
        try {
            for (Entry<String, URL> entry : selectedURLS.entrySet()) {
            String name = entry.getKey();
            if (name == null) {
                name = "";
            }

            IProject project = root.getProject(entry.getKey());
            if (project.exists()) {
                project.delete(true, null);
            }
            project.create(new NullProgressMonitor());
            project.open(new NullProgressMonitor());
            File directory = new File(FileLocator.toFileURL(
                entry.getValue()).getPath());
            if (directory.isDirectory()) {
                copy(directory.listFiles(), project, monitor);
            }

            }
        } catch (IOException | CoreException e) {
            e.printStackTrace();
        }
        return Status.OK_STATUS;
        }
    };
    job.schedule();
    return null;
    }

    private void copy(File[] files, IContainer container,
        IProgressMonitor monitor) {
    try {
        for (File file : files) {
        monitor.subTask("Copying " + file.getName());
        if (file.isDirectory()) {
            if (!file.getName().equals("CVS")) {//$NON-NLS-1$
            IFolder folder = container.getFolder(new Path(file
                .getName()));
            if (!folder.exists()) {
                folder.create(true, true, null);
                copy(file.listFiles(), folder, monitor);
            }
            }
        } else {
            IFile pFile = container.getFile(new Path(file.getName()));
            if (!pFile.exists()) {
            pFile.create(new FileInputStream(file), true,
                new NullProgressMonitor());
            }
            monitor.internalWorked(1);
        }

        }
    } catch (Exception e) {
        MessageDialog.openError(null, "Error",
            NLS.bind("Error happened during copy: \n{0}.", e));
    }
    }
}
