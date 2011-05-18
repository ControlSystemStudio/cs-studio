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
 */
package org.csstudio.alarm.treeView.ldap;

import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;

import org.csstudio.alarm.treeView.views.AbstractTreeModificationItem;
import org.csstudio.alarm.treeView.views.AlarmTreeModificationException;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;

/**
 * Modifies an LDAP component's attribute.
 *
 * @author bknerr
 * @since 09.12.2010
 */
public class ModifyLdapAttributeModificationItem extends AbstractTreeModificationItem {

    private final LdapName _ldapName;
    private final EpicsAlarmcfgTreeNodeAttribute _propId;
    private final String _attrValue;

    /**
     * Constructor.
     * @param value
     * @param string
     * @param ldapName
     */
    public ModifyLdapAttributeModificationItem(@Nonnull final LdapName ldapName,
                                               @Nonnull final EpicsAlarmcfgTreeNodeAttribute propId,
                                               @Nonnull final String value) {
        _ldapName = ldapName;
        _propId = propId;
        _attrValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDescription() {
        return "MODIFY LDAP ATTRIBUTE: " + _propId.getLdapAttribute() + "=" + _attrValue + " for node " + _ldapName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() throws AlarmTreeModificationException {
        final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
        if (service == null) {
            throw new AlarmTreeModificationException("Attribute modification failed.",
                                                     new ServiceUnavailableException("LDAP service unavailable."));
        }

        ModificationItem item = null;
        if (_attrValue.isEmpty()) {
            item = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(_propId.getLdapAttribute()));
        } else {
            item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                                        new BasicAttribute(_propId.getLdapAttribute(), _attrValue));
        }

        try {
            service.modifyAttributes(_ldapName, new ModificationItem[] {item});
        } catch (final NamingException e) {
            throw new AlarmTreeModificationException("MODIFY LDAP ATTRIBUTE FAILED: " + _propId.getLdapAttribute() + "=" + _attrValue + " for node " + _ldapName,
                                                     null);
        }
    }


}
