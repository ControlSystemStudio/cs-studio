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
package org.csstudio.alarm.treeview.views;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * Monitors the connection to the backend system and displays a message in the tree view if the
 * connection fails. When the connection is established or restored, triggers loading the
 * current state from the LDAP directory.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 09.06.2010
 */
public final class AlarmTreeConnectionMonitor implements IAlarmConnectionMonitor {

    private final AlarmTreeView _alarmTreeView;
    private final IAlarmSubtreeNode _monitorRootNode;

    /**
     *
     * Constructor.
     * @param rootNode the root node of the view
     */
    public AlarmTreeConnectionMonitor(@Nonnull final AlarmTreeView view,
                                      @Nonnull final IAlarmSubtreeNode rootNode) {
        _alarmTreeView = view;
        _monitorRootNode = rootNode;
    }

    @Override
    public void onConnect() {
        Display.getDefault().asyncExec(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                _alarmTreeView.getMessageArea().hide();

                // TODO (who?): This rebuilds the whole tree from
                // scratch. It would be better for the
                // usability to resynchronize only.
                _alarmTreeView.createAndScheduleImportInitialConfiguration(_monitorRootNode);
            }
        });
    }

    @Override
    public void onDisconnect() {
        Display.getDefault().asyncExec(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                final MessageArea area = _alarmTreeView.getMessageArea();
                area.showMessage(SWT.ICON_WARNING,
                                 "Connection error",
                                 "Some or all of the information displayed " +
                                 "may be outdated. The alarm tree is currently " +
                "not connected to all alarm servers.");
            }
        });
    }

}
