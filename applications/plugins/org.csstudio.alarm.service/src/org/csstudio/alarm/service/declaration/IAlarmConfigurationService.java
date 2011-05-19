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
package org.csstudio.alarm.service.declaration;

import java.io.FileNotFoundException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ExportContentModelException;


/**
 * Alarm configuration service.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 11.05.2010
 */
public interface IAlarmConfigurationService {

    /**
     * Retrieves the initial content model for the given facilities.
     *
     * @param facilityNames the list of the simple names of the facilities.
     * @return the content model
     * @throws CreateContentModelException
     * @throws LdapServiceException 
     */
    @Nonnull
    ContentModel<LdapEpicsAlarmcfgConfiguration> retrieveInitialContentModel(@Nonnull final List<String> facilityNames)
        throws CreateContentModelException, LdapServiceException;


    /**
     * Retrieves the initial content model for the given file.
     *
     * @param the filePath to the xml file
     * @return the content model
     * @throws CreateContentModelException  occurs on file not found, io error, or parsing error
     * @throws FileNotFoundException
     */
    @Nonnull
    ContentModel<LdapEpicsAlarmcfgConfiguration> retrieveInitialContentModelFromFile(@Nonnull final String filePath)
        throws CreateContentModelException, FileNotFoundException;


    /**
     * Exports the given content model to an xml file.
     * @param filePath the filePath to the new xml file.
     * @param model the content model to export
     * @param dtdFilePath the xml structure of the export file
     *
     * @throws ExportContentModelException error on trying to write the xml file
     */
    void exportContentModelToXmlFile(@Nonnull final String filePath,
                                     @Nonnull final ContentModel<LdapEpicsAlarmcfgConfiguration> model,
                                     @Nullable final String dtdFilePath)  throws ExportContentModelException;

}
