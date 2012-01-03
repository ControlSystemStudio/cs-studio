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

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.VIRTUAL_ROOT;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.INodeComponent;
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
public class DctContentModelBuilder extends
        AbstractContentModelBuilder<LdapEpicsAlarmcfgConfiguration> {

    private final IProject _dctPoject;

    /**
     * Constructor.
     */
    public DctContentModelBuilder(@Nonnull final IProject dctPoject) {
        _dctPoject = dctPoject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ContentModel<LdapEpicsAlarmcfgConfiguration> createContentModel()
            throws CreateContentModelException {
        final ContentModel<LdapEpicsAlarmcfgConfiguration> contentModel =
            new ContentModel<LdapEpicsAlarmcfgConfiguration>(
                VIRTUAL_ROOT);

        try {
            final ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> unit = addUnit(
                    contentModel, "EpicsAlarmcfg");
            addFacility(contentModel, _dctPoject.getName(), unit);
        } catch (final InvalidNameException e) {
            throw new CreateContentModelException(e.getMessage(), e);
        }

        for (final IRecord record : _dctPoject.getFinalRecords()) {
            final List<String> prototypeInstances = new ArrayList<String>();
            getParentPrototypeInstances(record.getContainer(),
                    prototypeInstances);
            try {
                addToContentModel(contentModel, prototypeInstances, record);
            } catch (final InvalidNameException e) {
                throw new CreateContentModelException(e.getMessage(), e);
            } catch (final AliasResolutionException e) {
                throw new CreateContentModelException(e.getMessage(), e);
            }
        }
        return contentModel;
    }

    /**
     * Add record with its parent prototype instances (components in LDAP) to
     * content model.
     *
     * @param contentModel
     * @param prototypeInstances
     * @param record
     * @throws InvalidNameException
     * @throws AliasResolutionException
     */
    private void addToContentModel(
            @Nonnull final ContentModel<LdapEpicsAlarmcfgConfiguration> contentModel,
            @Nonnull final List<String> prototypeInstances,
            @Nonnull final IRecord record) throws InvalidNameException,
            AliasResolutionException {

        final String ldapName = LdapUtils.createLdapName(
                FACILITY.getNodeTypeName(), _dctPoject.getName(),
                UNIT.getNodeTypeName(), _dctPoject.getName()).toString();
        ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> parent =
            (ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration>) contentModel.getChildByLdapName(ldapName);

        INodeComponent<LdapEpicsAlarmcfgConfiguration> newChild;
        for (final String instance : prototypeInstances) {
            newChild = null;
            final LdapName parentName = new LdapName(parent.getLdapName()
                    .getRdns());
            parentName.add(new Rdn(COMPONENT.getNodeTypeName(), instance));
            newChild = contentModel.getChildByLdapName(parentName.toString());
            if (newChild == null) {
                newChild = new TreeNodeComponent<LdapEpicsAlarmcfgConfiguration>(
                        instance, COMPONENT, parent, null, parentName);
                contentModel.addChild(parent, newChild);
            }
            parent = (ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration>) newChild;
        }
        final String epicsName = ResolutionUtil.resolve(
                AliasResolutionUtil.getEpicsNameFromHierarchy(record), record);
        final LdapName parentName = new LdapName(parent.getLdapName().getRdns());
        parentName.add(new Rdn(RECORD.getNodeTypeName(), epicsName));
        newChild = new TreeNodeComponent<LdapEpicsAlarmcfgConfiguration>(
                epicsName, RECORD, parent, null, parentName);
        contentModel.addChild(parent, newChild);
    }

    /**
     * Add unit to content model
     *
     * @param contentModel
     * @param projectName
     * @return
     * @throws InvalidNameException
     */
    private ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> addUnit(
            @Nonnull final ContentModel<LdapEpicsAlarmcfgConfiguration> contentModel,
            @Nonnull final String projectName) throws InvalidNameException {
        // LdapNameUtils.
        final LdapName ldapName = new LdapName(new Rdn(UNIT.getNodeTypeName(),
                _dctPoject.getName()).toString());
        ldapName.add(new Rdn(UNIT.getNodeTypeName(), _dctPoject.getName()));

        final ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> parent = contentModel
                .getVirtualRoot();
        ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> newChild;
        newChild = new TreeNodeComponent<LdapEpicsAlarmcfgConfiguration>(
                projectName, UNIT, parent, null, ldapName);
        contentModel.addChild(parent, newChild);
        return newChild;
    }

    /**
     * Add dct project name as facility to content model
     *
     * @param contentModel
     * @param projectName
     * @param unit
     * @throws InvalidNameException
     */
    private void addFacility(
            @Nonnull final ContentModel<LdapEpicsAlarmcfgConfiguration> contentModel,
            @Nonnull final String projectName,
            final ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> parent)
            throws InvalidNameException {
        final LdapName ldapName = LdapUtils.createLdapName(
                UNIT.getNodeTypeName(), _dctPoject.getName());
        ldapName.add(new Rdn(FACILITY.getNodeTypeName(), _dctPoject.getName()));

        ISubtreeNodeComponent<LdapEpicsAlarmcfgConfiguration> newChild;
        newChild = new TreeNodeComponent<LdapEpicsAlarmcfgConfiguration>(
                projectName, FACILITY, parent, null, ldapName);
        contentModel.addChild(parent, newChild);
    }

    /**
     * @param container
     *            Container of record
     * @return List of nested prototype instances starting with root
     */
    private void getParentPrototypeInstances(
            @Nonnull final IContainer container,
            @Nonnull final List<String> instances) {
        final String name = AliasResolutionUtil.getNameFromHierarchy(container);
        instances.add(0, name);
        if (container.getContainer() != null) {
            getParentPrototypeInstances(container.getContainer(), instances);
        }
    }
}
