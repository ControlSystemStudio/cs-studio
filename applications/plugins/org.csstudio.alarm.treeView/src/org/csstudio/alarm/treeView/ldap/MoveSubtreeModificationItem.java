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
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.views.AbstractTreeModificationItem;
import org.csstudio.alarm.treeView.views.AlarmTreeModificationException;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.treemodel.CreateContentModelException;

/**
 * Move subtree modification item. E.g. applied when 'Save in LDAP' action is triggered.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 21.06.2010
 */
public final class MoveSubtreeModificationItem extends AbstractTreeModificationItem {

    private static final ILdapService LDAP_SERVICE = AlarmTreePlugin.getDefault().getLdapService();


    private final LdapName _newLdapName;
    private final LdapName _oldNodeName;
    private final String _nodeSimpleName;
    private final String _nodeObjectClass;
    private final LdapName _targetName;

    /**
     * Constructor.
     * @param newLdapName
     * @param oldNodeName
     * @param nodeSimpleName
     * @param nodeObjectClass
     * @param targetName
     */
    MoveSubtreeModificationItem(@Nonnull final LdapName newLdapName,
                                @Nonnull final LdapName oldNodeName,
                                @Nonnull final String nodeSimpleName,
                                @Nonnull final String nodeObjectClass,
                                @Nonnull final LdapName targetName) {
        _newLdapName = newLdapName;
        _oldNodeName = oldNodeName;
        _nodeSimpleName = nodeSimpleName;
        _nodeObjectClass = nodeObjectClass;
        _targetName = targetName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply() throws AlarmTreeModificationException {
        try {
            _newLdapName.add(new Rdn(_nodeObjectClass, _nodeSimpleName));
            LDAP_SERVICE.move(LdapEpicsAlarmCfgObjectClass.ROOT, _oldNodeName, _newLdapName);
        } catch (final InvalidNameException e) {
            throw new AlarmTreeModificationException("New name could not be constructed as LDAP name.", e);
        } catch (final NamingException e) {
            throw new AlarmTreeModificationException("LDAP move action failed.", e);
        } catch (final CreateContentModelException e) {
            throw new AlarmTreeModificationException("LDAP move action failed. Content model for subtree could not be constructed.", e);
        }
        setApplied(true);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "!!!ATTENTION - MOVE ON NON-LEAF NODES NOT YET IMPLEMENTED!!!\nMOVE node " + _oldNodeName.toString() + " under " + _targetName.toString();
    }
}
