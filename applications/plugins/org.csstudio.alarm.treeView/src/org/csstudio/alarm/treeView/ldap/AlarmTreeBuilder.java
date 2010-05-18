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

import static org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants.EPICS_ALARM_CFG_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;

import java.sql.Date;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.model.ContentModel;
import org.csstudio.utility.ldap.model.ILdapBaseComponent;
import org.csstudio.utility.ldap.model.ILdapTreeComponent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * This class provides the Alarm Tree Mgmt methods to build the alarm tree.
 * On the {@link #build(SubtreeNode, IProgressMonitor)} method the full tree is
 * build including all nodes, whether they contain eren nodes or not.
 *
 * @author Joerg Rathlev, Jurij Kodre
 */
public final class AlarmTreeBuilder {

	/**
	 * The logger that is used by this class.
	 */
	private static final CentralLogger LOG = CentralLogger.getInstance();

	/**
     * Don't instantiate.
     */
    private AlarmTreeBuilder() {
        // Empty
    }

    private static String[] retrieveFacilityNames() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String facilitiesPref = prefs.getString(AlarmTreePlugin.PLUGIN_ID,
                                                      PreferenceConstants.FACILITIES, "", null);
        String[] facilityNames;
        if (facilitiesPref.equals("")) {
            facilityNames = new String[0];
        } else {
            facilityNames = facilitiesPref.split(";");
        }

        if (facilityNames.length == 0) {
            LOG.debug(AlarmTreeBuilder.class.getName(), "No facility names selected, using TEST facility.");
            facilityNames = new String[] { "TEST" };
        }
        return facilityNames;
    }


    private static void ensureTestFacilityExists(@Nonnull final DirContext ctx) {
        try {
            final LdapName testFacilityName = LdapUtils.createLdapQuery(EFAN_FIELD_NAME, "TEST",
                                                                        OU_FIELD_NAME,EPICS_ALARM_CFG_FIELD_VALUE);
            try {
                ctx.lookup(testFacilityName);
            } catch (final NameNotFoundException e) {
                LOG.info(AlarmTreeBuilder.class.getName(), "TEST facility does not exist in LDAP, creating it.");
                final Attributes attrs = new BasicAttributes();
                attrs.put(EFAN_FIELD_NAME, "TEST");
                attrs.put("objectClass", LdapEpicsAlarmCfgObjectClass.FACILITY.getDescription());
                attrs.put("epicsCssType", LdapEpicsAlarmCfgObjectClass.FACILITY.getCssType());
                ctx.bind(testFacilityName, null, attrs);
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
     * @param ctx
     * @param monitor
     * @return
     * @throws NamingException
     */
    private static boolean createAlarmSubtree(@Nonnull final SubtreeNode parentNode,
                                              @Nonnull final ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass> modelNode,
                                              @Nonnull final DirContext ctx,
                                              @Nullable final IProgressMonitor monitor) throws NamingException {

        final String simpleName = modelNode.getName();

        if (LdapEpicsAlarmCfgObjectClass.RECORD.equals(modelNode.getType())) {
            final ProcessVariableNode newNode = new ProcessVariableNode.Builder(simpleName).setParent(parentNode).build();
            // TODO (bknerr) : set alarm state has to be removed here
            //AlarmTreeNodeModifier.evaluateAttributes(modelNode.getAttributes(), newNode);
            AlarmTreeNodeModifier.setEpicsAttributes(newNode, modelNode.getAttributes());
            newNode.updateAlarm(new Alarm(simpleName, Severity.UNKNOWN, new Date(0L)));

        } else {
            final SubtreeNode newNode = new SubtreeNode.Builder(simpleName, modelNode.getType()).setParent(parentNode).build();
            for (final ILdapBaseComponent<LdapEpicsAlarmCfgObjectClass> child : modelNode.getDirectChildren()) {
                createAlarmSubtree(newNode, (ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass>) child, ctx, monitor);

                if ((monitor != null) && monitor.isCanceled()) {
                    return true;
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
     * @param rootNode
     * @param monitor
     * @return false if it has been canceled, true otherwise
     * @throws NamingException
     */
    public static boolean build(@Nonnull final SubtreeNode rootNode,
                                @Nullable final IProgressMonitor monitor) throws NamingException {
        final DirContext ctx = Engine.getInstance().getLdapDirContext();

        ensureTestFacilityExists(ctx);

        final IAlarmConfigurationService configService = AlarmTreePlugin.getDefault().getAlarmConfigurationService();

        final String[] facilityNames = retrieveFacilityNames();

        final ContentModel<LdapEpicsAlarmCfgObjectClass> model =
            configService.retrieveInitialContentModel(Arrays.asList(facilityNames));

//        final ContentModel<LdapEpicsAlarmCfgObjectClass> model =
//        configService.retrieveInitialContentModelFromFile("D:\\development\\bknerr\\workspace\\org.csstudio.alarm.treeView\\EpicsAlarmCfg.xml");

        for (final ILdapBaseComponent<LdapEpicsAlarmCfgObjectClass> node : model.getRoot().getDirectChildren()) {
            createAlarmSubtree(rootNode, (ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass>) node, ctx, monitor);
        }
        return true;
    }
}
