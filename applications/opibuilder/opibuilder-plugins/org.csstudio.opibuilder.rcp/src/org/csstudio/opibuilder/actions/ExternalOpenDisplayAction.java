/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.io.FileNotFoundException;
import java.util.logging.Level;

import org.csstudio.openfile.IOpenDisplayAction;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.product.LinkedResourcesJob;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

/**
 * Run OPI from external program, such as alarm GUI, data browser...
 * Extra data may be provided to provide macros and update shared links
 * before opening the OPI.
 *  @author Xihui Chen
 *  @author Kay Kasemir
 *  @author Will Rogers
 */
public class ExternalOpenDisplayAction implements IOpenDisplayAction
{
    private static final String SEPARATOR = "-share_link";

    /** Open OPI file.
     *  @param path the path of the OPI file, it can be a workspace path, file system path, URL
     *         or a opi file in opi search path.
     *  @param data the input macros and shared links in format of
     *  {@code "macro1 = hello", "macro2 = hello2" -share_link /eclipse/path=/filesystem/path}
     *  @throws Exception on error
     */
    @Override
    public void openDisplay(final String path, final String data) throws Exception
    {
        if (path == null || path.trim().isEmpty())
        {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "ExternalOpenDisplayAction for empty display");
            return;
        }

        IPath originPath = ResourceUtil.getPathFromString(path);
        if (!originPath.isAbsolute())
        {
            originPath = ResourceUtil.getFileOnSearchPath(originPath, false);
            if (originPath == null)
                throw new FileNotFoundException(NLS.bind("File {0} doesn't exist on search path.", path));
        }
        // Parse macros
        MacrosInput macrosInput = null;
        if (data == null)
        {
            // No macros or links.
            OpenTopOPIsAction.runOPI(null, originPath);
        }
        else
        {
            String[] parts = data.split(SEPARATOR);
            if (parts[0] != null && parts[0].trim().length() > 0)
            {
                // MacrosInput.recoverFromString(s) wants initial "true" for 'include_parent_macros'
                macrosInput = MacrosInput.recoverFromString("\"true\"," + parts[0]);
            }
            if (parts.length > 1 && parts[1] != null && parts[1].trim().length() > 0)
            {
                // The logic for creating links is wrapped in this job.  However,
                // we want the job to finish before continuing.
                final MacrosInput finalMacrosInput = macrosInput;
                final IPath finalOriginPath = originPath;
                final Job job = new LinkedResourcesJob(parts[1]);
                job.addJobChangeListener(new IJobChangeListener(){

                    @Override
                    public void aboutToRun(IJobChangeEvent jce) {}
                    @Override
                    public void awake(IJobChangeEvent jce) {}
                    @Override
                    public void running(IJobChangeEvent jce) {}
                    @Override
                    public void scheduled(IJobChangeEvent jce) {}
                    @Override
                    public void sleeping(IJobChangeEvent jce) {}

                    @Override
                    public void done(IJobChangeEvent jce) {
                        Runnable r = () -> { OpenTopOPIsAction.runOPI(finalMacrosInput, finalOriginPath); };
                        UIBundlingThread.getInstance().addRunnable(r);
                    }
                });
                job.schedule();
            }
            else
            {
                // We don't need to set up links.
                OpenTopOPIsAction.runOPI(macrosInput, originPath);
            }
        }
    }

}
