/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeview.views.actions;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.treeview.jobs.ImportXmlFileJob;
import org.csstudio.alarm.treeview.service.AlarmMessageListener;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * Import xml file action.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public final class ImportXmlFileAction extends Action {
    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ImportXmlFileAction.class);

    private final IWorkbenchPartSite _site;
    private final AlarmMessageListener _alarmListener;
    private final ImportXmlFileJob _importXmlFileJob;
    private final TreeViewer _viewer;

    /**
     * Constructor.
     * @param site
     * @param alarmListener
     * @param importXmlFileJob
     * @param viewer
     */
    ImportXmlFileAction(@Nonnull final IWorkbenchPartSite site,
                               @Nonnull final AlarmMessageListener alarmListener,
                               @Nonnull final ImportXmlFileJob importXmlFileJob,
                               @Nonnull final TreeViewer viewer) {
        _site = site;
        _alarmListener = alarmListener;
        _importXmlFileJob = importXmlFileJob;
        _viewer = viewer;
    }

    @Override
    public void run() {
        final String filePath = getFileNameToLoadFrom();
        if (filePath != null) {
            _importXmlFileJob.setXmlFilePath(filePath);
            LOG.debug("Starting XML file importer.");
            final IWorkbenchSiteProgressService progressService =
                (IWorkbenchSiteProgressService) _site.getAdapter(IWorkbenchSiteProgressService.class);

            // Set the tree to which updates are applied to null. This means updates
            // will be queued for later application.
            _alarmListener.suspendUpdateProcessing();
            // The directory is read in the background. Until then, set the viewer's
            // input to a placeholder object.
            _viewer.setInput(new Object[] {new PendingUpdateAdapter()});
            // Start the directory reader job.
            progressService.schedule(_importXmlFileJob, 0, true);
        }
    }

    @CheckForNull
    private String getFileNameToLoadFrom() {
        final FileDialog dialog = new FileDialog(_site.getShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] {"*.xml"});
        dialog.setText("Load alarm tree configuration file (.xml)");
        return dialog.open();
    }
}
