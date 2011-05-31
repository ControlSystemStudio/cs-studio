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
package org.csstudio.alarm.treeview.ldap;

import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;

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
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.alarm.treeview.model.Alarm;
import org.csstudio.alarm.treeview.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.model.IProcessVariableNodeListener;
import org.csstudio.alarm.treeview.model.ProcessVariableNode;
import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.alarm.treeview.model.TreeNodeSource;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
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
        final String facilityName = "Test";
        try {
            final LdapName testFacilityName = createLdapName(FACILITY.getNodeTypeName(), facilityName,
                                                             UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());

            final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
            if (service == null) {
                throw new ServiceUnavailableException("LDAP Service not available. Check for existing test facility failed.");
            }

            try {
                service.lookup(testFacilityName);
            } catch (final NameNotFoundException e) {
                LOG.info("Test facility named " + facilityName + " does not exist in LDAP, creating it.");
                final Attributes attrs = new BasicAttributes();
                attrs.put(FACILITY.getNodeTypeName(), facilityName);
                attrs.put(ATTR_FIELD_OBJECT_CLASS, FACILITY.getObjectClass());

                service.createComponent(testFacilityName, attrs);
            }
        } catch (final NamingException e) {
            LOG.error("Failed to create test facility named " + facilityName + "in LDAP", e);
        }
    }

    /**
     * Creates the initial alarm tree with all subtrees that end with records.
     * Can be canceled.
     * @param parentNode
     * @param pvNodeListener
     * @param modelNode
     * @param monitor
     * @return cancel status, true if canceled, false otherwise
     * @throws NamingException thrown if underlying jms cannot access attribute map or if name clash by child nodes is found
     */
    private static boolean createAlarmSubtree(@Nonnull final IAlarmSubtreeNode parentNode,
                                              @Nonnull final IProcessVariableNodeListener pvNodeListener,
                                              @Nonnull final INodeComponent<LdapEpicsAlarmcfgConfiguration> modelNode,
                                              @Nonnull final IProgressMonitor monitor, @Nonnull final TreeNodeSource source) throws NamingException {

        final String simpleName = modelNode.getName();
        IAlarmTreeNode newNode;

        if (RECORD.equals(modelNode.getType())) {
            guardForNameClash(parentNode, simpleName);
            newNode = new ProcessVariableNode.Builder(simpleName, source).setParent(parentNode).setListener(pvNodeListener).build();
            ((IAlarmProcessVariableNode) newNode).updateAlarm(new Alarm(simpleName, EpicsAlarmSeverity.UNKNOWN, new Date(0L)));
        } else {
            newNode = parentNode.getChild(simpleName);
            if (newNode == null) { // do not create a new subtree node if it already exists
                newNode = new SubtreeNode.Builder(simpleName, modelNode.getType(), source).setParent(parentNode).build();
            }

            if (modelNode instanceof ISubtreeNodeComponent) {
                final Collection<INodeComponent<LdapEpicsAlarmcfgConfiguration>> children =
                    ((ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration>) modelNode).getDirectChildren();

                for (final INodeComponent<LdapEpicsAlarmcfgConfiguration> child : children) {
                    createAlarmSubtree((IAlarmSubtreeNode) newNode, pvNodeListener, child, monitor, source);
                    if (monitor.isCanceled()) {
                        return true;
                    }
                }
            } else {
                throw new IllegalArgumentException("Node " + modelNode.getLdapName() + " is not an instance of " + ISubtreeNodeComponent.class.getName() +
                                                   ", but not of tree node type " + RECORD.getNodeTypeName() + " either!");
            }
        }

        // Attributes will be set on nodes and leaves. They contain help pages, css displays and the like
        final Attributes attributes = modelNode.getAttributes();
        AlarmTreeNodeModifier.setEpicsAttributes(newNode, attributes == null ? new BasicAttributes() : attributes);

        return false;
    }

    private static void guardForNameClash(@Nonnull final IAlarmSubtreeNode parentNode,
                                          @Nonnull final String simpleName) throws NamingException {
        if (!parentNode.canAddChild(simpleName)) {
            throw new NamingException("Record '" + simpleName + "' cannot be added to component '" + parentNode.getName() + "'." +
            		"\nCheck for name clash within the children of the component.");
        }
    }


    /**
     * Retrieves the alarm tree information for the facilities given in the
     * preferences and builds the alarm tree view data structure.
     * Returns the cancellation status, i.e. true if the build process has been
     * canceled.
     *
     * @param rootNode the root node for the alarm tree
     * @param pvNodeListener listener on the pv
     * @param model the content model
     * @param monitor the progress monitor
     * @return cancel status, true if it has been canceled, false otherwise
     * @throws NamingException
     */
    public static boolean build(@Nonnull final IAlarmSubtreeNode rootNode,
                                @Nonnull final IProcessVariableNodeListener pvNodeListener,
                                @Nonnull final ContentModel<LdapEpicsAlarmcfgConfiguration> model,
                                @Nonnull final IProgressMonitor monitor, @Nonnull final TreeNodeSource source) throws NamingException {
        ensureTestFacilityExists();

        for (final INodeComponent<LdapEpicsAlarmcfgConfiguration> ous : model.getVirtualRoot().getDirectChildren()) {
            // Level of ou=EpicsAlarmcfg

            // TODO (bknerr) : CR#1646 ensure in the fix that on this level only one child exists -> case insensitivity of LDAP may still lead to problems
            // LDAP does not distinguish between EpicsAlarmCfg and EpicsAlarmcfg which is considered here as two separate nodes
            final Collection<INodeComponent<LdapEpicsAlarmcfgConfiguration>> efans =
                ((ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration>) ous).getDirectChildren();

            for (final INodeComponent<LdapEpicsAlarmcfgConfiguration> efan : efans) {
                if (createAlarmSubtree(rootNode, pvNodeListener, efan, monitor, source)) {
                    return true;
                }
            }
        }
        return false;
    }
}
