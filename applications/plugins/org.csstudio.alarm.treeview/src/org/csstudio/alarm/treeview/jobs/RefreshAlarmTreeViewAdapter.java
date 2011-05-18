/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.alarm.treeview.jobs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.service.AlarmMessageListener;
import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Alarm tree view refresh adapter
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 20.05.2010
 */
class RefreshAlarmTreeViewAdapter extends JobChangeAdapter {

    private final AlarmTreeView _alarmTreeView;
    private final IAlarmSubtreeNode _adapterRootNode;

    /**
     * Constructor.
     * @param rootNode
     * @param alarmTreeView
     */
    public RefreshAlarmTreeViewAdapter(@Nonnull final AlarmTreeView alarmTreeView,
                                @Nonnull final IAlarmSubtreeNode rootNode) {
        _alarmTreeView = alarmTreeView;
        _adapterRootNode = rootNode;
    }

    @Override
    public void done(@Nullable final IJobChangeEvent innerEvent) {

        final AlarmMessageListener alarmListener = _alarmTreeView.getAlarmListener();

        alarmListener.startUpdateProcessing();

        _alarmTreeView.getSite().getShell().getDisplay().asyncExec(new Runnable() {
            @SuppressWarnings("synthetic-access")
			@Override
            public void run() {
                final TreeViewer viewer = _alarmTreeView.getViewer();
                if (viewer != null) {
                    viewer.setInput(_adapterRootNode);
                }
            }
        });
    }

}
