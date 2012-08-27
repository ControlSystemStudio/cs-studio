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


import java.util.Collections;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.localization.Messages;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Saves the current tree structure under this node in an XML file format.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 17.06.2010
 */
public final class SaveAsXmlAction extends Action {
    private static final Logger LOG = LoggerFactory.getLogger(SaveAsXmlAction.class);

    private final IWorkbenchPartSite _site;
    private final TreeViewer _viewer;

    /**
     * Constructor.
     * @param site
     * @param viewer
     */
    SaveAsXmlAction(@Nonnull final IWorkbenchPartSite site,
                    @Nonnull final TreeViewer viewer) {
        _site = site;
        _viewer = viewer;
    }

    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        final Object selected = selection.getFirstElement();
        if (selected instanceof SubtreeNode) {
            final SubtreeNode root = (SubtreeNode) selected;

            if (LdapEpicsAlarmcfgConfiguration.FACILITY.equals(root.getTreeNodeConfiguration())) {

                final String filePath = AlarmTreeViewActionFactory.getFileToSaveTo(_site);
                if (filePath != null) {
                    AlarmTreeViewActionFactory.createModelAndWriteXmlFile(_site, Collections.<IAlarmTreeNode>singletonList(root), filePath);
                }

            } else {
                LOG.error("Saving XML file is only possible on " + LdapEpicsAlarmcfgConfiguration.FACILITY.getObjectClass() + " type components."); //$NON-NLS-1$ //$NON-NLS-2$
                MessageDialog.openError(_site.getShell(),
                                        Messages.SaveAsXmlAction_Dialog_Title,
                                        Messages.SaveAsXmlAction_Dialog_Text);
            }
        }
    }
}
