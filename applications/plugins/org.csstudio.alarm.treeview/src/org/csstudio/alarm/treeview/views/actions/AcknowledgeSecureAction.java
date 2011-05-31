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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.treeview.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Acknowledge action.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public final class AcknowledgeSecureAction extends AbstractUserDependentAction {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(AcknowledgeSecureAction.class);

    private static final String RIGHT_ID = "operating";
    private static final boolean DEFAULT_PERMISSION = true;

    private final TreeViewer _viewer;

    /**
     * Constructor.
     * @param viewer
     */
    AcknowledgeSecureAction(@Nonnull final TreeViewer viewer) {
        super(RIGHT_ID, DEFAULT_PERMISSION);
        _viewer = viewer;
    }

    @Override
    protected void doWork() {
        final Set<Map<String, String>> messages = new HashSet<Map<String, String>>();

        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();

        for (final Iterator<?> i = selection.iterator(); i.hasNext();) {
            final Object o = i.next();
            if (o instanceof IAlarmSubtreeNode) {
                final IAlarmSubtreeNode snode = (IAlarmSubtreeNode) o;
                for (final IAlarmProcessVariableNode pvnode : snode.collectUnacknowledgedAlarms()) {
                    final String name = pvnode.getName();
                    final EpicsAlarmSeverity severity = pvnode.getUnacknowledgedAlarmSeverity();
                    final Map<String, String> properties = new HashMap<String, String>();
                    properties.put("NAME", name);
                    properties.put("SEVERITY", severity.toString());
                    messages.add(properties);
                }
            } else if (o instanceof IAlarmProcessVariableNode) {
                final IAlarmProcessVariableNode pvnode = (IAlarmProcessVariableNode) o;
                final String name = pvnode.getName();
                final EpicsAlarmSeverity severity = pvnode.getUnacknowledgedAlarmSeverity();
                final Map<String, String> properties = new HashMap<String, String>();
                properties.put("NAME", name);
                properties.put("SEVERITY", severity.toString());
                messages.add(properties);
            }
        }
        if (!messages.isEmpty()) {
            LOG.debug("Scheduling send acknowledgement (" + messages.size() + " messages)");
            final SendAcknowledge ackJob = SendAcknowledge.newFromProperties(messages);
            ackJob.schedule();
        }
    }
}
