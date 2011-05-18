/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
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
package org.csstudio.alarm.treeview.views;

import java.util.Date;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.model.Alarm;
import org.csstudio.alarm.treeview.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * An update in response to an alarm or acknowledgement message which has not yet been applied to
 * the tree.
 *
 * @author Joerg Rathlev
 */
public abstract class AbstractPendingUpdate {

    /**
     * Applies this update.
     *
     * @param updater
     *            the updater which will be used to apply this update.
     */
    public abstract void apply();

    /**
     * Creates an update which will update the alarm tree based on an acknowledgement message.
     *
     * @param name
     *            the name of the node to which the acknowledgement will apply.
     * @param treeRoot
     * @return an update which will apply the acknowledgement.
     */
    @Nonnull
    public static AbstractPendingUpdate createAcknowledgementUpdate(@Nonnull final String name,
                                                                    @Nonnull final IAlarmSubtreeNode treeRoot) {
        return new AbstractPendingUpdate() {
            @Override
            public void apply() {
                for (final IAlarmProcessVariableNode node : treeRoot.findProcessVariableNodes(name)) {
                    node.acknowledgeAlarm();
                }
                refreshView();

            }

            @Override
            @Nonnull
            public String toString() {
                return "PendingUpdate[Acknowledgement,name=" + name + "]";
            }
        };
    }

    /**
     * Creates an update which will update the alarm tree based on an alarm message.
     *
     * @param name
     *            the name of the node to which the alarm will apply.
     * @param severity
     *            the severity of the alarm.
     * @param eventtime
     *            the eventtime of the alarm.
     * @param treeRoot
     * @return an update which will apply the alarm.
     */
    @Nonnull
    public static AbstractPendingUpdate createAlarmUpdate(@Nonnull final String name,
                                                          @Nonnull final EpicsAlarmSeverity severity,
                                                          @Nonnull final Date eventtime,
                                                          @Nonnull final IAlarmSubtreeNode treeRoot) {
        return new AbstractPendingUpdate() {
            @Override
            public void apply() {
                for (final IAlarmProcessVariableNode node : treeRoot.findProcessVariableNodes(name)) {
                    final Alarm alarm = new Alarm(name, severity, eventtime);
                    node.updateAlarm(alarm);
                }
                refreshView();
            }

            @Override
            @Nonnull
            public String toString() {
                return new StringBuilder("PendingUpdate[Alarm,name=").append(name)
                        .append(",severity=").append(severity).append("]").toString();
            }
        };
    }


    /**
     * Refreshes the alarm tree view.
     */
    public static void refreshView() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                // FIXME: improve this!
                final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow();
                if (activeWorkbenchWindow != null) {
                    final IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
                    if (page != null) {
                        final IViewPart view = page.findView(AlarmTreeView.getID());
                        if (view instanceof AlarmTreeView) {
                            ((AlarmTreeView) view).refresh();
                        }
                    }
                }
            }
        });
    }
}
