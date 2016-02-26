/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.opibuilder.validation.Activator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationRunner;

/**
 *
 * <code>QuickFixer</code> is a resolution implementation that fixes the OPI validation failures, by replacing the
 * actual value with the expected value. It only works for the markers which represent a read-only validation failure.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
@SuppressWarnings("restriction")
public class QuickFixer implements IMarkerResolutionGenerator2 {

    private static final Logger LOGGER = Logger.getLogger(QuickFixer.class.getName());

    private static class Resolution extends WorkbenchMarkerResolution {

        private final IMarker marker;

        Resolution(IMarker marker) {
            this.marker = marker;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.ui.IMarkerResolution2#getDescription()
         */
        @Override
        public String getDescription() {
            // not used anyway
            return getLabel();
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.ui.IMarkerResolution2#getImage()
         */
        @Override
        public Image getImage() {
            return Activator.getInstance().getQuickFixImage();
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.ui.IMarkerResolution#getLabel()
         */
        @Override
        public String getLabel() {
            return "Change the value of the property to the expected value and save OPI. OPIs might be backed up during the process.";
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
         */
        @Override
        public void run(IMarker marker) {
            throw new UnsupportedOperationException("Run with a single marker should never be called.");
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.ui.views.markers.WorkbenchMarkerResolution#run(org.eclipse.core.resources.IMarker[],
         * org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        public void run(final IMarker[] markers, final IProgressMonitor monitor) {
            Set<IResource> resources = new HashSet<>();
            for (IMarker m : markers) {
                resources.add(m.getResource());
            }
            try {
                if (!Utilities.shouldContinueIfFileOpen("quick fix",
                    resources.toArray(new IResource[resources.size()]))) {
                    monitor.setCanceled(true);
                    return;
                }
            } catch (PartInitException e) {
                LOGGER.log(Level.SEVERE, "Could not obtain editor inputs.", e);
                monitor.setCanceled(true);
                return;
            }

            final boolean doBackup;
            if (Activator.getInstance().isShowBackupDialog()) {
                doBackup = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Backup",
                    "Would you like to make a backup of the OPI files before changing them?");
            } else {
                doBackup = Activator.getInstance().isDoBackup();
            }

            Job job = Job.create("OPI Validation Quick Fix", mmonitor -> {
                try {
                    Map<IPath, List<ValidationFailure>> toFix = new HashMap<>();
                    ValidationFailure f;
                    List<ValidationFailure> list;
                    // sort all failures by paths, so that each file is edited only once
                    for (int i = 0; i < markers.length; i++) {
                        f = (ValidationFailure) markers[i].getAttribute(Validator.ATTR_VALIDATION_FAILURE);
                        list = toFix.get(f.getPath());
                        if (list == null) {
                            list = new ArrayList<>();
                            toFix.put(f.getPath(), list);
                        }
                        list.add(f);
                    }
                    mmonitor.beginTask("OPI Validation Quick Fix", toFix.size() + 3);
                    mmonitor.worked(1);

                    // if requested to do backup, copy all quick-fixed files to <file>~
                    if (doBackup) {
                        for (IPath path : toFix.keySet()) {
                            IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                            File file = ifile.getLocation().toFile();
                            String bck = file.getAbsolutePath();
                            File backup = new File(bck + "~");
                            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    mmonitor.worked(1);
                    for (List<ValidationFailure> l : toFix.values()) {
                        // one call per file
                        SchemaFixer.fixOPIFailure(l.toArray(new ValidationFailure[l.size()]));
                        mmonitor.worked(1);
                    }
                    for (IMarker m : markers) {
                        // refresh all changed files
                        m.getResource().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
                    }
                    // revalidated all changed files to get rid of the fixed validations
                    revalidate(markers, mmonitor);
                    mmonitor.done();
                    return Status.OK_STATUS;
                } catch (CoreException | IOException e) {
                    LOGGER.log(Level.WARNING, "Unexpected error trying to quick fix the OPIs.", e);
                    Display.getDefault()
                        .asyncExec(() -> MessageDialog.openError(Display.getDefault().getActiveShell(),
                            "Error Fixing OPI Problem",
                            "There was an unexpected error while trying to quick fix the OPI: " + e.getMessage()));
                    return new Status(IStatus.ERROR, Activator.ID,
                        "There was an unexpected error while trying to quick fix the OPIs.", e);
                }
            });

            job.schedule();
        }

        /**
         * Revalidated all opis defined by the given markers. This method is called after successful quick fix.
         *
         * @param markers the markers that define the OPI files that will be validated
         * @param monitor the monitor to report progress to
         * @throws CoreException if resource could not be extracted from the marker
         */
        private void revalidate(IMarker[] markers, IProgressMonitor monitor) throws CoreException {
            Map<IProject, Set<IResource>> map = new HashMap<>();
            IProject p;
            for (IMarker m : markers) {
                p = m.getResource().getProject();
                Set<IResource> set = map.get(p);
                if (set == null) {
                    set = new HashSet<>();
                    map.put(p, set);
                }
                set.add(m.getResource());
            }
            boolean isClearMarkers = Activator.getInstance().isClearMarkers();
            boolean isShowSummary = Activator.getInstance().isShowSummaryDialog();
            try {
                // in case of revalidation after quick fix, do not clear the markers
                Activator.getInstance().getPreferenceStore().setValue(Activator.PREF_CLEAR_MARKERS, false);
                Activator.getInstance().getPreferenceStore().setValue(Activator.PREF_SHOW_SUMMARY, false);
                ValidationRunner.validate(map, ValType.Manual, monitor, true);
            } finally {
                Activator.getInstance().getPreferenceStore().setValue(Activator.PREF_CLEAR_MARKERS, isClearMarkers);
                Activator.getInstance().getPreferenceStore().setValue(Activator.PREF_SHOW_SUMMARY, isShowSummary);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.eclipse.ui.views.markers.WorkbenchMarkerResolution#findOtherMarkers(org.eclipse.core.resources.IMarker[])
         */
        @Override
        public IMarker[] findOtherMarkers(IMarker[] markers) {
            List<IMarker> list = new ArrayList<>();
            for (IMarker m : markers) {
                try {
                    if (m == this.marker || !Validator.MARKER_PROBLEM.equals(m.getType())
                        || !((ValidationFailure) m.getAttribute(Validator.ATTR_VALIDATION_FAILURE)).isFixable()) {
                        continue;
                    }
                    list.add(m);
                } catch (CoreException e) {
                    // ignore
                }
            }

            return list.toArray(new IMarker[list.size()]);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IMarkerResolutionGenerator#getResolutions(org.eclipse.core.resources.IMarker)
     */
    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        return new IMarkerResolution[] { new Resolution(marker) };
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IMarkerResolutionGenerator2#hasResolutions(org.eclipse.core.resources.IMarker)
     */
    @Override
    public boolean hasResolutions(IMarker marker) {
        try {
            return ((ValidationFailure) marker.getAttribute(Validator.ATTR_VALIDATION_FAILURE)).isFixable();
        } catch (CoreException e) {
            return false;
        }
    }

}
