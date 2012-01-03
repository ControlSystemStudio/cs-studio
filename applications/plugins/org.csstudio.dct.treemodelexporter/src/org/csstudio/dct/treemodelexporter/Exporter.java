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

import java.io.File;
import java.io.IOException;

import org.csstudio.dct.export.IExporter;
import org.csstudio.dct.model.IProject;
import org.csstudio.utility.treemodel.ContentModelExporter;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ExportContentModelException;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DCT Treemodel exporter called via extension point from DCT.
 *
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 22.06.2010
 */
public class Exporter implements IExporter {

    private static final Logger LOG = LoggerFactory.getLogger(Exporter.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String export(final IProject project) {


        final DctContentModelBuilder builder = new DctContentModelBuilder(project);
        try {
            builder.build();
        } catch (final CreateContentModelException e) {
            LOG.error("Error building content model from DCT, " + e.getMessage());
        }

        final Bundle bundle = TreemodelExporterActivator.getDefault().getBundle();
        File loc = null;
        try {
            loc = FileLocator.getBundleFile(bundle);
        } catch (final IOException e) {
            LOG.error("Cannot resolve bundle, " + e.getMessage());
        }

        final String dtdFilePath = new File(loc, "dtd/epicsAlarmCfg.dtd").toString();
        String xmlFile = null;
        try {
            xmlFile = ContentModelExporter.exportContentModelToXmlString(builder.getModel(), dtdFilePath);
        } catch (final ExportContentModelException e) {
            LOG.error("Error exporting content model, " + e.getMessage());
        }

        return xmlFile;
    }
}
