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
 *
 * $Id$
 */
package org.csstudio.alarm.service.internal;

import static org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants.EPICS_ALARM_CFG_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.directory.SearchControls;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.model.builder.LdapContentModelBuilder;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.ContentModelExporter;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ExportContentModelException;
import org.csstudio.utility.treemodel.builder.XmlFileContentModelBuilder;

/**
 * Alarm configuration service implementation
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 11.05.2010
 */
public class AlarmConfigurationServiceImpl implements IAlarmConfigurationService {

    private final ILdapService _ldapService;

    /**
     * Constructor.
     * @param ldapService access to LDAP
     */
    public AlarmConfigurationServiceImpl(@Nonnull final ILdapService ldapService) {
        _ldapService = ldapService;
    }

    /**
     * {@inheritDoc}
     * @throws CreateContentModelException
     */
    @Override
    @Nonnull
    public ContentModel<LdapEpicsAlarmCfgObjectClass> retrieveInitialContentModel(@Nonnull final List<String> facilityNames) throws CreateContentModelException {

        ContentModel<LdapEpicsAlarmCfgObjectClass> model;
        try {
            model = new ContentModel<LdapEpicsAlarmCfgObjectClass>(LdapEpicsAlarmCfgObjectClass.ROOT,
                                                                   EPICS_ALARM_CFG_FIELD_VALUE);
        } catch (final InvalidNameException e) {
            throw new CreateContentModelException("Error creating empty content model.", e);
        }

        final LdapContentModelBuilder<LdapEpicsAlarmCfgObjectClass> builder =
            new LdapContentModelBuilder<LdapEpicsAlarmCfgObjectClass>(model);

        for (final String facility : facilityNames) {
            final LdapSearchResult result =
                _ldapService.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(EFAN_FIELD_NAME, facility,
                                                                                         OU_FIELD_NAME, EPICS_ALARM_CFG_FIELD_VALUE),
                                                                                         "(objectClass=*)",
                                                                                         SearchControls.SUBTREE_SCOPE);
            if (result != null) {
                builder.setSearchResult(result);
                builder.build();
            }
        }
        final ContentModel<LdapEpicsAlarmCfgObjectClass> enrichedModel = builder.getModel();
        return enrichedModel != null ? enrichedModel : model;
    }

    /**
     * {@inheritDoc}
     * @throws CreateContentModelException occurs on file not found, io error, or parsing error.
     * @throws InvalidNameException
     */
    @Override
    @CheckForNull
    public ContentModel<LdapEpicsAlarmCfgObjectClass> retrieveInitialContentModelFromFile(@Nonnull final String filePath)
        throws CreateContentModelException {

        final XmlFileContentModelBuilder<LdapEpicsAlarmCfgObjectClass> builder =
            new XmlFileContentModelBuilder<LdapEpicsAlarmCfgObjectClass>(LdapEpicsAlarmCfgObjectClass.ROOT, filePath);
        builder.build();
        return builder.getModel();
    }





    /**
     * {@inheritDoc}
     */
    @Override
    public void exportContentModelToXmlFile(@Nonnull final String filePath,
                                            @Nonnull final ContentModel<LdapEpicsAlarmCfgObjectClass> model,
                                            @Nullable final String dtdFilePath) throws ExportContentModelException {
        ContentModelExporter.exportContentModelToXmlFile(filePath, model, dtdFilePath);
    }

}
