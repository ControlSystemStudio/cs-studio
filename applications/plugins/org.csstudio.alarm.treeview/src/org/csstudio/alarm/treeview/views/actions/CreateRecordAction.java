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

import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.treeview.ldap.DirectoryEditor;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.csstudio.alarm.treeview.views.ITreeModificationItem;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Create record action.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public final class CreateRecordAction extends AbstractCreateComponentAction {

    private final AlarmTreeView _alarmTreeView;

    /**
     * Constructor.
     * @param site
     * @param viewer
     * @param alarmTreeView
     * @param modificationItems
     */
    CreateRecordAction(@Nonnull final IWorkbenchPartSite site,
                       @Nonnull final TreeViewer viewer,
                       @Nonnull final AlarmTreeView alarmTreeView,
                       @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        super(site, viewer, modificationItems);
        _alarmTreeView = alarmTreeView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    protected ITreeModificationItem createComponent(@Nonnull final IAlarmSubtreeNode parent,
                                                    @Nonnull final String name) {
        // the precondition of DirectoryEditor.createProcessVariableRecord has already been checked in AbstractCreateComponentAction
        // before the call to createComponent
        ITreeModificationItem result = DirectoryEditor.createProcessVariableRecord(parent, name, _alarmTreeView.getPVNodeListener());
        IAlarmConnection connection = _alarmTreeView.getConnection();
        if (connection != null) {
            connection.registerPV(name);
        }
        return result;
    }
}
