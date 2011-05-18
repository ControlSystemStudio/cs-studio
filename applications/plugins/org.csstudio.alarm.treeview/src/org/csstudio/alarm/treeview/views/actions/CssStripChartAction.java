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

import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.platform.ui.util.EditorUtil;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

/**
 * Css strip chart action.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public final class CssStripChartAction extends Action {
    private final TreeViewer _viewer;
    private final IWorkbenchPartSite _site;

    /**
     * Constructor.
     * @param viewer
     * @param site
     */
    CssStripChartAction(@Nonnull final TreeViewer viewer,
                               @Nonnull final IWorkbenchPartSite site) {
        _viewer = viewer;
        _site = site;
    }

    @Override
    public void run() {
        final IAlarmTreeNode node = getSelectedNode();
        if (node != null) {
            final IPath path = new Path(node.getInheritedPropertyWithUrlProtocol(EpicsAlarmcfgTreeNodeAttribute.CSS_STRIP_CHART));

            // The following code assumes that the path is relative to
            // the Eclipse workspace.
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            final IWorkbenchPage page = _site.getPage();
            try {
                EditorUtil.openEditor(page, file);
            } catch (final PartInitException e) {
                MessageDialog.openError(_site.getShell(), "Alarm Tree", e.getMessage());
            }
        }
    }

    /**
     * Returns the node that is currently selected in the tree.
     *
     * @return the selected node, or <code>null</code> if the selection is empty or the selected
     *         node is not of type <code>IAlarmTreeNode</code>.
     */
    @CheckForNull
    private IAlarmTreeNode getSelectedNode() {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        final Object selected = selection.getFirstElement();
        if (selected instanceof IAlarmTreeNode) {
            return (IAlarmTreeNode) selected;
        }
        return null;
    }
}
