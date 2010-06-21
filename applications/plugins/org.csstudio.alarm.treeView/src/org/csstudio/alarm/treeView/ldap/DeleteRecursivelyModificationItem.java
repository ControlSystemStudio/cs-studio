/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
package org.csstudio.alarm.treeView.ldap;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.views.AbstractTreeModificationItem;
import org.csstudio.alarm.treeView.views.AlarmTreeModificationException;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.treemodel.CreateContentModelException;

/**
 * Modification item that deletes a given LDAP component including its subtree.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 21.06.2010
 */
public final class DeleteRecursivelyModificationItem extends AbstractTreeModificationItem {

    private static final ILdapService LDAP_SERVICE = AlarmTreePlugin.getDefault().getLdapService();

    private final IAlarmTreeNode _node;
    private final LdapName _nodeName;

    /**
     * Constructor.
     * @param node
     * @param nodeName
     */
    DeleteRecursivelyModificationItem(@Nonnull final IAlarmTreeNode node,
                                              @Nonnull final LdapName nodeName) {
        _node = node;
        _nodeName = nodeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "DELETE NODE " + _node.getLdapName().toString();
    }

    @Override
    public boolean apply() throws AlarmTreeModificationException {
        boolean result;
        try {
            result = LDAP_SERVICE.removeComponent(LdapEpicsAlarmCfgObjectClass.ROOT, _nodeName);
        } catch (final InvalidNameException e) {
            throw new AlarmTreeModificationException("New name could not be constructed as LDAP name.", e);
        } catch (final CreateContentModelException e) {
            throw new AlarmTreeModificationException("Content model could not be constructed for subtree of " + _nodeName.toString(), e);
        }
        setApplied(result);
        return result;
    }
}
