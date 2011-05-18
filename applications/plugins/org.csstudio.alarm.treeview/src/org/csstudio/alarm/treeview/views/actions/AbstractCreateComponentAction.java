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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.alarm.treeview.views.ITreeModificationItem;
import org.csstudio.alarm.treeview.views.LdapNameInputValidator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Create Component action.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public abstract class AbstractCreateComponentAction extends Action {
    private final IWorkbenchPartSite _site;
    private final TreeViewer _viewer;
    private final Queue<ITreeModificationItem> _ldapModificationItems;

    /**
     * Constructor.
     * @param site
     * @param viewer
     * @param modificationItems
     */
    AbstractCreateComponentAction(@Nonnull final IWorkbenchPartSite site,
                                  @Nonnull final TreeViewer viewer,
                                  @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        _site = site;
        _viewer = viewer;
        _ldapModificationItems = modificationItems;
    }

    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        final Object selected = selection.getFirstElement();
        if (selected instanceof SubtreeNode) {
            final SubtreeNode parent = (SubtreeNode) selected;
            final String name = promptForRecordName();
            if ( (name != null) && !name.equals("")) {
                if (parent.canAddChild(name)) {
                    final ITreeModificationItem item = createComponent(parent, name);
                    if (item != null) {
                        _ldapModificationItems.add(item);
                    }
                    _viewer.refresh(parent);
                } else {
                    String message = "Node '" + name + "' cannot be added to component '"
                            + parent.getName() + "'.\n" + "Does it already exist?";
                    MessageDialog.openWarning(_site.getShell(), "Error adding record", message);
                }
            } // else ignore, cancel was pressed or empty string
        }
    }

    /**
     * @return null if no useful value was entered, else the value
     */
    @CheckForNull
    private String promptForRecordName() {
        final InputDialog dialog = new InputDialog(_site.getShell(),
                                                   "Create New Component",
                                                   "Component name:",
                                                   null,
                                                   new LdapNameInputValidator());
        if (Window.OK == dialog.open()) {
            return dialog.getValue();
        }
        return null;
    }

    @Nonnull
    protected abstract ITreeModificationItem createComponent(@Nonnull final IAlarmSubtreeNode parent,
                                                             @Nonnull final String name);
}
