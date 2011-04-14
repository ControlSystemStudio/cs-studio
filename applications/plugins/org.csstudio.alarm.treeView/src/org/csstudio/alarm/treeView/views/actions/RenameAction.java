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
package org.csstudio.alarm.treeView.views.actions;

import java.util.Collections;
import java.util.Queue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.ldap.DirectoryEditException;
import org.csstudio.alarm.treeView.ldap.DirectoryEditor;
import org.csstudio.alarm.treeView.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.PVNodeItem;
import org.csstudio.alarm.treeView.views.ITreeModificationItem;
import org.csstudio.alarm.treeView.views.LdapNameInputValidator;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * The rename action.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public final class RenameAction extends Action {
    private static final Logger LOG = CentralLogger.getInstance().getLogger(RenameAction.class);
    
    private final TreeViewer _viewer;
    private final IWorkbenchPartSite _site;
    private final Queue<ITreeModificationItem> _ldapModificationItems;
    
    /**
     * Constructor.
     * @param viewer
     * @param site
     * @param ldapModificationItems
     */
    RenameAction(@Nonnull final TreeViewer viewer,
                 @Nonnull final IWorkbenchPartSite site,
                 @Nonnull final Queue<ITreeModificationItem> ldapModificationItems) {
        _viewer = viewer;
        _site = site;
        _ldapModificationItems = ldapModificationItems;
    }

    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        final IAlarmTreeNode selected = (IAlarmTreeNode) selection.getFirstElement();
        final String name = promptForNewName(selected.getName());
        if (name != null) {
            try {
                final ITreeModificationItem modItem = DirectoryEditor.rename(selected, name);
                if (modItem != null) {
                    _ldapModificationItems.add(modItem);
                }
                if (selected instanceof IAlarmProcessVariableNode) {
                    retrieveInitialAlarmState((IAlarmProcessVariableNode) selected);
                }
            } catch (final DirectoryEditException e) {
                MessageDialog.openError(_site.getShell(),
                                        "Rename",
                                        "Could not rename the entry: " + e.getMessage());
            }
            refreshViewer(selected);
        }
    }

    @CheckForNull
    private String promptForNewName(@Nullable final String oldName) {
        final InputDialog dialog = new InputDialog(_site.getShell(),
                                                   "Rename",
                                                   "Name:",
                                                   oldName,
                                                   new LdapNameInputValidator());
        if (Window.OK == dialog.open()) {
            return dialog.getValue();
        }
        return null;
    }

    private void retrieveInitialAlarmState(@Nonnull final IAlarmProcessVariableNode pvNode) {
        final IAlarmInitItem initItem = new PVNodeItem(pvNode);
        
        final IAlarmService alarmService = AlarmTreePlugin.getDefault().getAlarmService();
        if (alarmService != null) {
            alarmService.retrieveInitialState(Collections.singletonList(initItem));
        } else {
            LOG.warn("Initial state could not be retrieved because alarm service is not available.");
        }
    }

    private void refreshViewer(@Nonnull final IAlarmTreeNode selected) {
        if (selected.getParent() != null) {
            _viewer.refresh(selected.getParent());
        } else {
            _viewer.update(selected, null);
        }
    }

}
