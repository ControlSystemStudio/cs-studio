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

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_ALARM_CFG_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EREN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.CompositeName;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.AbstractAlarmTreeNode;
import org.csstudio.alarm.treeView.model.LdapObjectClass;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.reader.LDAPReader.LdapSearchParams;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * This job reads the record entries (eren) from the LDAP directory and creates
 * the nodes representing them in the tree. The parent nodes are also created,
 * but their attributes are not read by this job. Empty nodes which do not
 * contain any eren objects are not created by this job.
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

	/**
	 * Evaluates the attributes (if any) of an object found in the
	 * directory. If there is an alarm, triggers the alarm for the node
	 * in the alarm tree.
	 *
	 * @param result the object found in the directory.
	 * @param node the node on which the alarm must be triggered.
	 * @throws NamingException if something bad happens...
	 */
	private static void evaluateAttributes(@Nonnull final SearchResult result,
	                                       @Nonnull final ProcessVariableNode node) throws NamingException {
		final Attributes attrs = result.getAttributes();
		TreeBuilder.setAlarmState(node, attrs);
		setEpicsAttributes(node, attrs);
	}


	/**
	 * Sets the EPICS attributes of the given node based on the given
	 * attributes.
	 *
	 * @param node
	 *            the node.
	 * @param attrs
	 *            the attributes.
	 * @throws NamingException
	 *             if an error occurs.
	 */
	public static void setEpicsAttributes(@Nonnull final AbstractAlarmTreeNode node,
	                                       @Nonnull final Attributes attrs) throws NamingException {

		final Attribute alarmDisplayAttr = attrs.get("epicsCssAlarmDisplay");
		if (alarmDisplayAttr != null) {
			final String display = (String) alarmDisplayAttr.get();
			if (display != null) {
				node.setCssAlarmDisplay(display);
			}
		}

		final Attribute helpPageAttr = attrs.get("epicsHelpPage");
		if (helpPageAttr != null) {
			final String helpPage = (String) helpPageAttr.get();
			if ((helpPage != null) && helpPage.matches("^http://.+")) {
				try {
					node.setHelpPage(new URL(helpPage));
				} catch (final MalformedURLException e) {
					LOG.warn(AlarmTreeBuilder.class.getName(), "epicsHelpPage attribute for node "
							+ node + " contains a malformed URL");
				}
			}
		}

		final Attribute helpGuidanceAttr = attrs.get("epicsHelpGuidance");
		if (helpGuidanceAttr != null) {
			final String helpGuidance = (String) helpGuidanceAttr.get();
			if (helpGuidance != null) {
				node.setHelpGuidance(helpGuidance);
			}
		}

		final Attribute displayAttr = attrs.get("epicsCssDisplay");
		if (displayAttr != null) {
			final String display = (String) displayAttr.get();
			if (display != null) {
				node.setCssDisplay(display);
			}
		}

		final Attribute chartAttr = attrs.get("epicsCssStripChart");
		if (chartAttr != null) {
			final String chart = (String) chartAttr.get();
			if (chart != null) {
				node.setCssStripChart(chart);
			}
		}
	}


	/**
	 * Creates a new node with the given name and inserts it into the tree.
	 * @param name the object's relative name. This name will be used to determine
	 *        where in the tree to put the object.
	 * @return the tree item for the given object.
	 */
	private static ProcessVariableNode createTreeNode(@Nonnull final LdapName name,
	                                                  @Nonnull final SubtreeNode treeRoot) {
        final SubtreeNode parentNode = TreeBuilder.findCreateParentNode(treeRoot, name);
		final String sname = LdapNameUtils.simpleName(name);
		final ProcessVariableNode node = new ProcessVariableNode(parentNode, sname);
		return node;
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
            final String testFacilityName = LdapUtils.createLdapQuery(EFAN_FIELD_NAME, "TEST",
                                                                      OU_FIELD_NAME,EPICS_ALARM_CFG_FIELD_VALUE);
            try {
                ctx.lookup(testFacilityName);
            } catch (final NameNotFoundException e) {
                LOG.info(AlarmTreeBuilder.class.getName(), "TEST facility does not exist in LDAP, creating it.");
                final Attributes attrs = new BasicAttributes();
                attrs.put(EFAN_FIELD_NAME, "TEST");
                attrs.put("objectClass", LdapObjectClass.FACILITY.getObjectClassName());
                attrs.put("epicsCssType", LdapObjectClass.FACILITY.getCssType());
                ctx.bind(testFacilityName, null, attrs);
            }
        } catch (final NamingException e) {
            LOG.error("Failed to create TEST facility in LDAP", e);
        }
    }

    /**
     * Returns either the search results for those subtrees of which the leafs
     * are records or <code>null</code>
     * if the calling job has been canceled.
     *
     * @param facilityNames .
     * @param monitor .
     * @return either a list of search results for the subtrees of the given
     *         facilities or <code>null</code>
     */
    @CheckForNull
    private static List<LdapSearchResult> retrieveFacilitySubTrees(@Nonnull final String[] facilityNames,
                                                                   @Nullable final IProgressMonitor monitor) {
        final List<LdapSearchResult> results = new ArrayList<LdapSearchResult>(facilityNames.length);

        for (final String facility : facilityNames) {
            final LdapSearchResult result =
                LDAPReader.getSearchResultSynchronously(
                                new LdapSearchParams(LdapUtils.createLdapQuery(EFAN_FIELD_NAME, facility,
                                                                               OU_FIELD_NAME, EPICS_ALARM_CFG_FIELD_VALUE),
                                                     any(EREN_FIELD_NAME)));

            results.add(result);
            if ((monitor != null) && monitor.isCanceled()) {
                return null;
            }
        }
        return results;
    }


    /**
     * Creates the initial alarm tree with all subtrees that end with records.
     * Can be canceled.
     *
     * @param rootNode .
     * @param results the ldap search results for the subtrees
     * @param ctx ldap context
     * @param monitor the job monitor providing the cancellation info
     * @return false if it has been canceled, true otherwise
     * @throws NamingException
     */
    private static boolean createInitialAlarmTree(@Nonnull final SubtreeNode rootNode,
                                                  @Nonnull final List<LdapSearchResult> results,
                                                  @Nonnull final DirContext ctx,
                                                  @Nullable final IProgressMonitor monitor) throws NamingException {
        final NameParser nameParser = ctx.getNameParser(new CompositeName());
        // retrieve results from all reads
        for (final LdapSearchResult searchResult : results) {
            for (final SearchResult row : searchResult.getAnswerSet()) {

                final LdapName name = (LdapName) nameParser.parse(row.getNameInNamespace());


                name.remove(0); // removes DE
                name.remove(0); // removes whatever
                name.remove(0);

                final ProcessVariableNode node = AlarmTreeBuilder.createTreeNode(name, rootNode);

                // Read the object's alarm status, and trigger an alarm on the node
                // that was just created if there is an alarm.
                AlarmTreeBuilder.evaluateAttributes(row, node);
            }
            if ((monitor != null) && monitor.isCanceled()) {
                return true;
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

        final String[] facilityNames = retrieveFacilityNames();

        final List<LdapSearchResult> results = retrieveFacilitySubTrees(facilityNames, monitor);
        if (results == null) {
            return true;
        }

        return createInitialAlarmTree(rootNode, results, ctx, monitor);
    }

}
