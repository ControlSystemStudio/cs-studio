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
import java.util.Map;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.model.ProcessVariableNode;
import org.csstudio.alarm.treeview.preferences.AlarmTreePreference;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RunCssAlarmDisplayAction
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 17.06.2010
 */
public final class RunCssAlarmDisplayAction extends Action {
    private static final Logger LOG = LoggerFactory.getLogger(RunCssAlarmDisplayAction.class);

    private final TreeViewer _viewer;

    /**
     * Constructor.
     * @param viewer
     */
    RunCssAlarmDisplayAction(@Nonnull final TreeViewer viewer) {
        _viewer = viewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        final Object selected = selection.getFirstElement();
        if (selected instanceof IAlarmTreeNode) {
            final IAlarmTreeNode node = (IAlarmTreeNode) selected;
            final IPath path = new Path(node.getInheritedPropertyWithUrlProtocol(EpicsAlarmcfgTreeNodeAttribute.CSS_ALARM_DISPLAY));
            final Map<String, String> aliases = new HashMap<String, String>();
            if (node instanceof ProcessVariableNode) {
                String key = AlarmTreePreference.ALARM_DISPLAY_ALIAS.getValue();
                aliases.put(key, node.getName());
            }
            LOG.debug("Opening display: " + path);
            RunModeService.getInstance().openDisplayShellInRunMode(path, aliases);
        }
    }
}
