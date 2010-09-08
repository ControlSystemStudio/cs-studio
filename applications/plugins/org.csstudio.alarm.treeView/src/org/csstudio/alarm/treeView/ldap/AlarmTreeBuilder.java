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

import static org.csstudio.alarm.service.declaration.LdapEpicsAlarmcfgConfiguration.FACILITY;
import static org.csstudio.alarm.service.declaration.LdapEpicsAlarmcfgConfiguration.RECORD;
import static org.csstudio.alarm.service.declaration.LdapEpicsAlarmcfgConfiguration.ROOT;
import static org.csstudio.utility.ldap.utils.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.utils.LdapUtils.createLdapQuery;

import java.sql.Date;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.alarm.service.declaration.Severity;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.model.TreeNodeSource;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.INodeComponent;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class provides the Alarm Tree Mgmt methods to build the alarm tree.
 * On the {@link #build(SubtreeNode, IProgressMonitor)} method the full tree is
 * build including all nodes, whether they contain eren nodes or not.
 *
 * @author Bastian Knerr
 */
public final class AlarmTreeBuilder {

    /**
     * The logger that is used by this class.
     */
    private static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmTreeBuilder.class);


    /**
     * Don't instantiate.
     */
    private AlarmTreeBuilder() {
        // Empty
    }

    private static void ensureTestFacilityExists() throws ServiceUnavailableException {
        try {
            final LdapName testFacilityName = createLdapQuery(FACILITY.getNodeTypeName(), "TEST",
                                                              ROOT.getNodeTypeName(), ROOT.getRootTypeValue());

            final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
            if (service == null) {
                throw new ServiceUnavailableException("LDAP Service not available. Check for existing test facility failed.");
            }

            try {
                service.lookup(testFacilityName);
            } catch (final NameNotFoundException e) {
                LOG.info("TEST facility does not exist in LDAP, creating it.");
                final Attributes attrs = new BasicAttributes();
                attrs.put(FACILITY.getNodeTypeName(), "TEST");
                attrs.put(ATTR_FIELD_OBJECT_CLASS, FACILITY.getDescription());

                service.createComponent(testFacilityName, attrs);
            }
        } catch (final NamingException e) {
            LOG.error("Failed to create TEST facility in LDAP", e);
        }
    }

    /**
     * Creates the initial alarm tree with all subtrees that end with records.
     * Can be canceled.
     * @param parentNode
     * @param modelNode
     * @param monitor
     * @return
     * @throws NamingException
     */
    private static boolean createAlarmSubtree(@Nonnull final IAlarmSubtreeNode parentNode,
                                              @Nonnull final INodeComponent<LdapEpicsAlarmcfgConfiguration> modelNode,
                                              @Nonnull final IProgressMonitor monitor,
                                              @Nonnull final TreeNodeSource source) throws NamingException {

        final String simpleName = modelNode.getName();

        if (RECORD.equals(modelNode.getType())) {
            final ProcessVariableNode newNode = new ProcessVariableNode.Builder(simpleName, source).setParent(parentNode).build();

            final Attributes attributes = modelNode.getAttributes();
            AlarmTreeNodeModifier.setEpicsAttributes(newNode, attributes == null ? new BasicAttributes() : attributes);
            newNode.updateAlarm(new Alarm(simpleName, Severity.UNKNOWN, new Date(0L)));

        } else {
            final SubtreeNode newNode = new SubtreeNode.Builder(simpleName, modelNode.getType(), source).setParent(parentNode).build();
            if (modelNode instanceof ISubtreeNodeComponent) {
                final Collection<INodeComponent<LdapEpicsAlarmcfgConfiguration>> children =
                    ((ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration>) modelNode).getDirectChildren();

                for (final INodeComponent<LdapEpicsAlarmcfgConfiguration> child : children) {
                    createAlarmSubtree(newNode, child, monitor, source);
                    if (monitor.isCanceled()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    /**
     * Retrieves the alarm tree information for the facilities given in the
     * preferences and builds the alarm tree view data structure.
     * Returns the cancellation status, i.e. true if the build process has been
     * cancelled.
     *
     * @param rootNode the root node for the alarm tree
     * @param model the content model
     * @param monitor the progress monitor
     * @return false if it has been canceled, true otherwise
     * @throws NamingException
     */
    public static boolean build(@Nonnull final IAlarmSubtreeNode rootNode,
                                @Nonnull final ContentModel<LdapEpicsAlarmcfgConfiguration> model,
                                @Nonnull final IProgressMonitor monitor,
                                @Nonnull final TreeNodeSource source) throws NamingException {
        ensureTestFacilityExists();

        for (final INodeComponent<LdapEpicsAlarmcfgConfiguration> node : model.getRoot().getDirectChildren()) {
            createAlarmSubtree(rootNode, node, monitor, source);
        }
        return true;
    }
}
