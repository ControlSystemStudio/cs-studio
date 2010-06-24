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
package org.csstudio.dct.treemodelexporter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;
import org.csstudio.utility.treemodel.TreeNodeComponent;
import org.csstudio.utility.treemodel.builder.AbstractContentModelBuilder;

/**
 * Build content model from dct export.
 *
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 22.06.2010
 */
public class DctContentModelBuilder extends AbstractContentModelBuilder<LdapEpicsAlarmcfgConfiguration> {

    private final IProject _dctPoject;

    /**
     * Constructor.
     */
    public DctContentModelBuilder(@Nonnull IProject dctPoject) {
        _dctPoject = dctPoject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ContentModel<LdapEpicsAlarmcfgConfiguration> createContentModel() throws CreateContentModelException {
        ContentModel<LdapEpicsAlarmcfgConfiguration> contentModel = null;
        try {
            contentModel = new ContentModel<LdapEpicsAlarmcfgConfiguration>(LdapEpicsAlarmcfgConfiguration.ROOT);
        } catch (InvalidNameException e) {
            throw new CreateContentModelException(e.getMessage(), e);
        }

        try {
            addFacility(contentModel, _dctPoject.getName());
        } catch (InvalidNameException e) {
            throw new CreateContentModelException(e.getMessage(), e);
        }
        
        for (IRecord record : _dctPoject.getFinalRecords()) {
            List<String> prototypeInstances = new ArrayList<String>();
            getParentPrototypeInstances(record.getContainer(), prototypeInstances);
            try {
                addToContentModel(contentModel, prototypeInstances, record);
            } catch (InvalidNameException e) {
                throw new CreateContentModelException(e.getMessage(), e);
            } catch (AliasResolutionException e) {
                throw new CreateContentModelException(e.getMessage(), e);
            }
        }
        return contentModel;
    }

    /**
     * Add record with its parent prototype instances (components in LDAP) to content model.
     * 
     * @param contentModel
     * @param prototypeInstances
     * @param record
     * @throws InvalidNameException 
     * @throws AliasResolutionException 
     */
    private void addToContentModel(@Nonnull ContentModel<LdapEpicsAlarmcfgConfiguration> contentModel,
                                   @Nonnull List<String> prototypeInstances,
                                   @Nonnull IRecord record) throws InvalidNameException, AliasResolutionException {
        ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> parent = contentModel.getChildByLdapName("efan=" + _dctPoject.getName() + 
                                                                                                       ",ou=" + AlarmTreeLdapConstants.EPICS_ALARM_CFG_FIELD_VALUE);
        ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> newChild;
        for (String instance : prototypeInstances) {
            newChild = null;
            final LdapName ldapName = new LdapName(parent.getLdapName().getRdns());
            ldapName.add(new Rdn(LdapEpicsAlarmcfgConfiguration.COMPONENT.getNodeTypeName(),instance));
            newChild = contentModel.getChildByLdapName(ldapName.toString());
            if (newChild ==null) {
                newChild = new TreeNodeComponent<LdapEpicsAlarmcfgConfiguration>(instance, 
                                                                             LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                                                             parent, null, ldapName);
                contentModel.addChild(parent, newChild);
            }
            parent = newChild;
        }
        String epicsName = ResolutionUtil.resolve(AliasResolutionUtil.getEpicsNameFromHierarchy(record), record);
        final LdapName ldapName = new LdapName(parent.getLdapName().getRdns());
        ldapName.add(new Rdn(LdapEpicsAlarmcfgConfiguration.RECORD.getNodeTypeName(), epicsName));
        newChild = new TreeNodeComponent<LdapEpicsAlarmcfgConfiguration>(epicsName, LdapEpicsAlarmcfgConfiguration.RECORD,
                parent, null, ldapName);
        contentModel.addChild(parent, newChild);
    }
    
    /**
     * Add dct project name as facility to content model
     * 
     * @param contentModel
     * @param projectName
     * @throws InvalidNameException 
     */
    private void addFacility(@Nonnull ContentModel<LdapEpicsAlarmcfgConfiguration> contentModel, @Nonnull String projectName) throws InvalidNameException {
        final LdapName ldapName = new LdapName(contentModel.getRoot().getLdapName().getRdns());
        ldapName.add(new Rdn(LdapEpicsAlarmcfgConfiguration.FACILITY.getNodeTypeName(),_dctPoject.getName()));

        ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> parent = contentModel.getRoot();
        ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> newChild;
        newChild = new TreeNodeComponent<LdapEpicsAlarmcfgConfiguration>(projectName, 
                LdapEpicsAlarmcfgConfiguration.FACILITY,
                parent, 
                null, 
                ldapName);
            contentModel.addChild(parent, newChild);
    }

    

    

    /**
     * @param container Container of record
     * @return List of nested prototype instances starting with root
     */
    private void getParentPrototypeInstances(@Nonnull IContainer container, @Nonnull List<String> instances) {
        String name = AliasResolutionUtil.getNameFromHierarchy(container);
        instances.add(0, name);
        if (container.getContainer() != null) {
            getParentPrototypeInstances(container.getContainer(), instances);
        }
    }

}
