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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.model.ContentModel;
import org.csstudio.utility.ldap.model.ContentModelExporter;
import org.csstudio.utility.ldap.model.ExportContentModelException;
import org.csstudio.utility.ldap.model.ILdapTreeComponent;
import org.csstudio.utility.ldap.model.ImportContentModelException;
import org.csstudio.utility.ldap.model.LdapTreeComponent;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

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
     * @throws InvalidNameException
     */
    @Override
    @Nonnull
    public ContentModel<LdapEpicsAlarmCfgObjectClass> retrieveInitialContentModel(@Nonnull final List<String> facilityNames) throws InvalidNameException {

        final ContentModel<LdapEpicsAlarmCfgObjectClass> model =
            new ContentModel<LdapEpicsAlarmCfgObjectClass>(LdapEpicsAlarmCfgObjectClass.ROOT);

        for (final String facility : facilityNames) {
            final LdapSearchResult result =
                _ldapService.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(EFAN_FIELD_NAME, facility,
                                                                                         OU_FIELD_NAME, EPICS_ALARM_CFG_FIELD_VALUE),
                                                                                         "(objectClass=*)",
                                                                                         SearchControls.SUBTREE_SCOPE);

            model.addSearchResult(result);
        }
        return model;
    }

    /**
     * {@inheritDoc}
     * @throws ImportContentModelException occurs on file not found, io error, or parsing error.
     * @throws InvalidNameException
     */
    @Override
    @CheckForNull
    public ContentModel<LdapEpicsAlarmCfgObjectClass> retrieveInitialContentModelFromFile(@Nonnull final String filePath) throws ImportContentModelException {

        try {
            final FileInputStream fstream = new FileInputStream(filePath);
            // Convert our input stream to a
            // DataInputStream
            final DataInputStream in = new DataInputStream(fstream);

            final SAXBuilder builder = new SAXBuilder();
            final Document doc = builder.build(in);

            return createContentModelFromFile(doc);

        } catch (final FileNotFoundException e) {
            throw new ImportContentModelException("File " + filePath + " could not be found.", e);
        } catch (final JDOMException e) {
            throw new ImportContentModelException("File " + filePath + " contains parsing errors.", e);
        } catch (final IOException e) {
            throw new ImportContentModelException("File " + filePath + " could not be parsed due to I/O error.", e);
        }
    }

    /**
     * @param doc the xml document model
     * @throws ImportContentModelException if Rdn or LdapName could not be constructed
     */
    @Nonnull
    private ContentModel<LdapEpicsAlarmCfgObjectClass> createContentModelFromFile(@Nonnull final Document doc)
        throws ImportContentModelException {

        ContentModel<LdapEpicsAlarmCfgObjectClass> model = null;
        try {
            model = new ContentModel<LdapEpicsAlarmCfgObjectClass>(LdapEpicsAlarmCfgObjectClass.ROOT);
        } catch (final InvalidNameException e) {
            throw new ImportContentModelException("Component model could not be constructed. Invalid LDAP name for root element.", e);
        }
        final Element rootElement = doc.getRootElement();

        // Leave out the root element
        final List<Element> elements = getChildrenElements(rootElement);
        for (final Element child :  elements) {
            processElement(model, child, model.getRoot());
        }
        return model;
    }


    @SuppressWarnings("unchecked")
    @Nonnull
    private List<Element> getChildrenElements(@Nonnull final Element element) {
        final List<Element> children = element.getContent(new ElementFilter());
        return children;
    }

    /**
     * Recursive
     * @param model
     * @param element
     * @param iLdapTreeComponent
     * @throws ImportContentModelException if Rdn or LdapName could not be constructed
     */
    private void processElement(@Nonnull final ContentModel<LdapEpicsAlarmCfgObjectClass> model,
                                @Nonnull final Element element,
                                @Nonnull final ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass> ldapParent) throws ImportContentModelException {


        final String type = element.getName();
        final LdapEpicsAlarmCfgObjectClass oc = LdapEpicsAlarmCfgObjectClass.ROOT.getObjectClassByRdnType(type);
        final String name = element.getAttributeValue("name");

        final List<Rdn> rdns = new ArrayList<Rdn>(ldapParent.getLdapName().getRdns());
        try {
            rdns.add(new Rdn(type, name));
        } catch (final InvalidNameException e) {
            throw new ImportContentModelException("Rdn for type=" + type + " and name=" + name + " could not be constructed.", e);
        }
        final LdapName fullName = new LdapName(rdns);

        if (model.getByTypeAndLdapName(oc, fullName.toString()) == null) {
            ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass> newLdapChild;
            try {
                newLdapChild = new LdapTreeComponent<LdapEpicsAlarmCfgObjectClass>(name,
                        oc,
                        oc.getNestedContainerClasses(),
                        ldapParent,
                        new BasicAttributes(),
                        fullName);
            } catch (final InvalidNameException e) {
                throw new ImportContentModelException("Component model with LdapName " + fullName + " could not be constructed. Invalid LDAP name.", e);

            }
            model.addChild(ldapParent, newLdapChild);
        }
        final ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass> ldapComponent =
            (ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass>) model.getByTypeAndLdapName(oc, fullName.toString());

        final List<Element> children = getChildrenElements(element);

        // cycle through all immediate elements under the rootElement
        for (final Element child : children) {
            processElement(model, child, ldapComponent);
        }
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
