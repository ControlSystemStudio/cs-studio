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

import java.util.Queue;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.treeview.model.PropertySourceAdapterFactory;
import org.csstudio.alarm.treeview.service.AlarmMessageListener;
import org.csstudio.alarm.treeview.views.ITreeModificationItem;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * Reloads the current tree structure from LDAP.
 * Current modifications of the alarm tree view that have not been explicitly stored in LDAP are lost.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 17.06.2010
 */
public final class ReloadFromLdapAction extends Action {
    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ReloadFromLdapAction.class);

    private final IWorkbenchPartSite _site;
    private final TreeViewer _viewer;
    private final AlarmMessageListener _alarmListener;
    private final Queue<ITreeModificationItem> _ldapModificationItems;
    private final Job _directoryReaderJob;

    /**
     * Constructor.
     * @param site
     * @param viewer
     * @param alarmListener
     * @param ldapModificationItems
     * @param directoryReaderJob
     */
    ReloadFromLdapAction(@Nonnull final IWorkbenchPartSite site,
                         @Nonnull final TreeViewer viewer,
                         @Nonnull final AlarmMessageListener alarmListener,
                         @Nonnull final Queue<ITreeModificationItem> ldapModificationItems,
                         @Nonnull final Job directoryReaderJob) {
        _site = site;
        _viewer = viewer;
        _alarmListener = alarmListener;
        _ldapModificationItems = ldapModificationItems;
        _directoryReaderJob = directoryReaderJob;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        boolean willReload = true;
        if (!_ldapModificationItems.isEmpty()) {
            willReload = MessageDialog
                    .openConfirm(_site.getShell(), "Unsaved items", getMessageWithUnsavedItems()
                            + "\nDo you want to reload from LDAP (Ok) or keep unsaved items (Cancel)?");
        }
        if (willReload) {
            reloadFromLdap();
        }
    }
    
    private String getMessageWithUnsavedItems() {
        boolean single = _ldapModificationItems.size() == 1;
        String result = "There " + (single ? "is " : "are ") + _ldapModificationItems.size()
                + " unsaved item" +  (single ? "." : "s.");
        return result;
    }

    private void reloadFromLdap() {
        // Remove all recent modifications from the queue
        _ldapModificationItems.clear();

        // Reset the selection cache for the property view
        PropertySourceAdapterFactory.dirty();

        LOG.debug("Starting directory reader.");
        final IWorkbenchSiteProgressService progressService =
            (IWorkbenchSiteProgressService) _site.getAdapter(IWorkbenchSiteProgressService.class);

        // This means updates will be queued for later application.
        _alarmListener.suspendUpdateProcessing();

        // The directory is read in the background. Until then, set the viewer's
        // input to a placeholder object.
        _viewer.setInput(new Object[] {new PendingUpdateAdapter()});

        // Start the directory reader job.
        progressService.schedule(_directoryReaderJob, 0, true);
    }
}
