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
package org.csstudio.alarm.treeView.views;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.service.AlarmMessageListener;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * Alarm tree view refresh adapter
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 20.05.2010
 */
public class RefreshAlarmTreeViewAdapter extends JobChangeAdapter {

    private final AlarmTreeView _alarmTreeView;
    private final SubtreeNode _adapterRootNode;

    /**
     * Constructor.
     * @param rootNode
     * @param alarmTreeView TODO
     */
    RefreshAlarmTreeViewAdapter(@Nonnull final AlarmTreeView alarmTreeView,
                                @Nonnull final SubtreeNode rootNode) {
        _alarmTreeView = alarmTreeView;
        _adapterRootNode = rootNode;
    }

    @Override
    public void done(@Nullable final IJobChangeEvent innerEvent) {

        // TODO jp-mc retrieveInitialStateSynchronously not enabled
        //            _alarmTreeView.retrieveInitialStateSynchronously(_rootNode);

        _alarmTreeView.asyncSetViewerInput(_adapterRootNode); // Display the new tree.

        final AlarmMessageListener alarmListener = _alarmTreeView.getAlarmListener();

        alarmListener.setUpdater(new AlarmTreeUpdater(_adapterRootNode));

        _alarmTreeView.getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                RefreshAlarmTreeViewAdapter.this._alarmTreeView.getViewer().refresh();
            }
        });
    }
}
