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
package org.csstudio.alarm.treeView.jobs;

import static org.csstudio.utility.ldap.service.util.LdapNameUtils.parseSearchResult;
import static org.csstudio.utility.ldap.service.util.LdapNameUtils.removeRdns;
import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.UNIT;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.treeView.ldap.AlarmTreeBuilder;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.TreeNodeSource;
import org.csstudio.alarm.treeView.views.AlarmTreeView;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.util.LdapNameUtils.Direction;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.INodeComponent;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Job to import an XML file of the alarm tree.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 19.05.2010
 */
public final class ImportXmlFileJob extends Job {
    
    private final IAlarmConfigurationService _configService;
    private final IAlarmSubtreeNode _rootNode;
    private List<IAlarmSubtreeNode> _xmlRootNodes = null;
    private String _filePath;
    private final AlarmTreeView _alarmTreeView;
    
    /**
     * Constructor.
     * @param alarmTreeView
     * @param configService
     * @param rootNode
     * @param ldapService
     */
    public ImportXmlFileJob(@Nonnull final AlarmTreeView alarmTreeView,
                            @Nonnull final IAlarmConfigurationService configService,
                            @Nonnull final IAlarmSubtreeNode rootNode) {
        super("ImportFileJob");
        _alarmTreeView = alarmTreeView;
        _configService = configService;
        _rootNode = rootNode;
    }
    
    public void setXmlFilePath(@Nonnull final String filePath) {
        _filePath = filePath;
    }
    
    @Override
    @Nonnull
    protected IStatus run(@Nonnull final IProgressMonitor monitor) {
        monitor.beginTask("Reading alarm tree from XML file", IProgressMonitor.UNKNOWN);
        
        try {
            _xmlRootNodes = new ArrayList<IAlarmSubtreeNode>();
            final ContentModel<LdapEpicsAlarmcfgConfiguration> model = _configService
                    .retrieveInitialContentModelFromFile(_filePath);
            
            final IStatus status = checkForExistingFacilities(model, _rootNode);
            if (!status.isOK()) {
                return status;
            }
            
            final boolean canceled = AlarmTreeBuilder.build(_rootNode,
                                                            _alarmTreeView.getPVNodeListener(),
                                                            model,
                                                            monitor,
                                                            TreeNodeSource.XML);
            if (canceled) {
                return Status.CANCEL_STATUS;
            }
            
            retrieveXMLRootNodes();
        } catch (final CreateContentModelException e) {
            return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID, "Could not import file: "
                    + e.getMessage(), e);
        } catch (final NamingException e) {
            return new Status(IStatus.ERROR,
                              AlarmTreePlugin.PLUGIN_ID,
                              "Could not properly build the full alarm tree: " + e.getMessage(),
                              e);
        } catch (final FileNotFoundException e) {
            return new Status(IStatus.ERROR,
                              AlarmTreePlugin.PLUGIN_ID,
                              "Could not properly open the input file stream: " + e.getMessage(),
                              e);
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;
    }
    
    /**
     * the top level children with source XML are collected
     */
    private void retrieveXMLRootNodes() {
        List<IAlarmTreeNode> children = _rootNode.getChildren();
        for (IAlarmTreeNode child : children) {
            if ( (child instanceof IAlarmSubtreeNode) && (child.getSource() == TreeNodeSource.XML)) {
                _xmlRootNodes.add((IAlarmSubtreeNode) child);
            }
        }
        
    }
    
    /**
     * Checks whether a facility from the imported XML file does already exist in the LDAP (might
     * not be visible in the current alarm tree view if not set in the preferences) or in the current
     * view itself (in case an XML file containing this facility identifier has been imported before).
     *
     * @param model the content model
     * @param rootNode the root node of the alarm tree view
     * @return an error status if a facility does already exist, otherwise OK
     *
     * @throws NamingException
     */
    @Nonnull
    private IStatus checkForExistingFacilities(@Nonnull final ContentModel<LdapEpicsAlarmcfgConfiguration> model,
                                               @Nonnull final IAlarmSubtreeNode rootNode) throws NamingException {
        
        final Set<String> existingFacilityNames = new HashSet<String>();
        
        existingFacilityNames.addAll(getExistingFacilityNamesFromLdap());
        
        existingFacilityNames.addAll(getExistingFacilitiesFromView(rootNode));
        
        final Map<String, INodeComponent<LdapEpicsAlarmcfgConfiguration>> facilityMap = model.getByType(FACILITY);
        
        existingFacilityNames.retainAll(facilityMap.keySet());
        
        if (!existingFacilityNames.isEmpty()) {
            return new Status(IStatus.ERROR,
                              AlarmTreePlugin.PLUGIN_ID,
                              "Following facility names from XML file already exist in the current view or in LDAP.\n"
                                      + "Please rename them in your file to import:\n"
                                      + existingFacilityNames.toString(),
                              null);
        }
        return Status.OK_STATUS;
    }
    
    @Nonnull
    private Set<String> getExistingFacilitiesFromView(@Nonnull final IAlarmSubtreeNode rootNode) {
        final Set<String> facilities = new HashSet<String>();
        for (final IAlarmTreeNode facilityNode : rootNode.getChildren()) {
            facilities.add(facilityNode.getLdapName().toString());
        }
        return facilities;
    }
    
    @Nonnull
    private Set<String> getExistingFacilityNamesFromLdap() throws NamingException {
        
        final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
        if (service == null) {
            throw new ServiceUnavailableException("LDAP service not available. Existing facilities could not be retrieved from LDAP.");
        }
        
        final ILdapSearchResult searchResult = service
                .retrieveSearchResultSynchronously(createLdapName(UNIT.getNodeTypeName(),
                                                                  UNIT.getUnitTypeValue()),
                                                   any(FACILITY.getNodeTypeName()),
                                                   SearchControls.ONELEVEL_SCOPE);
        final Set<SearchResult> set = searchResult.getAnswerSet();
        final Set<String> facilityNamesInLdap = new HashSet<String>();
        for (final SearchResult row : set) {
            final LdapName fullLdapName = parseSearchResult(row);
            final LdapName partLdapName = removeRdns(fullLdapName,
                                                     UNIT.getNodeTypeName(),
                                                     Direction.FORWARD);
            facilityNamesInLdap.add(partLdapName.toString());
        }
        return facilityNamesInLdap;
    }
    
    @CheckForNull
    public List<IAlarmSubtreeNode> getXmlRootNodes() {
        return _xmlRootNodes;
    }
}
