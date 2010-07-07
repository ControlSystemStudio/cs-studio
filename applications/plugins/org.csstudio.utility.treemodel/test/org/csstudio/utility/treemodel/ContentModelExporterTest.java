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
package org.csstudio.utility.treemodel;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.treemodel.builder.TestTreeConfigurator;
import org.csstudio.utility.treemodel.builder.XmlFileContentModelBuilder;
import org.csstudio.utility.treemodel.builder.XmlFileContentModelBuilderTest;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.eclipse.core.runtime.FileLocator;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * Tests the export functionality of the content model exporter.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 05.07.2010
 */
public class ContentModelExporterTest {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ContentModelExporterTest.class);

    private static final String TEST_EXPORT_XML = "Test_Export.xml";

    private static ContentModel<TestTreeConfigurator> MODEL;
    private static File RES_PATH;
    private static String VALID_XML;
    private static Document IMPORTED_DOC;
    private Document _exportedDoc;

    private final Format f = Format.getPrettyFormat();
    private final XMLOutputter _outputter = new XMLOutputter(f);

    @BeforeClass
    public static final void buildResourcePath() {
        try {
            final Bundle bundle = Activator.getDefault().getBundle();
            final File loc = FileLocator.getBundleFile(bundle);

            RES_PATH = new File(loc, "testres");

            LOG.error("Resource path: " + RES_PATH.toString());

        } catch (final IOException e1) {
            Assert.fail("File locator could not deliver bundle file path.");
        }

        VALID_XML = new File(RES_PATH, XmlFileContentModelBuilderTest.TEST_VALID_XML).getAbsolutePath();
        final XmlFileContentModelBuilder<TestTreeConfigurator> builder =
            new XmlFileContentModelBuilder<TestTreeConfigurator>(TestTreeConfigurator.ROOT, VALID_XML);
        try {
            builder.build();
            MODEL = builder.getModel();
        } catch (final Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        }

        // Get DOM tree of imported test file via JDOM
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(VALID_XML);
            final DataInputStream in = new DataInputStream(fstream);
            final SAXBuilder saxBuilder = new SAXBuilder(true);
            IMPORTED_DOC = saxBuilder.build(in);
        } catch (final FileNotFoundException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        } catch (final JDOMException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        } catch (final IOException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        }
    }


    @Test
    public void testExportContentModelToFile() {

        final File expFile = new File(RES_PATH, TEST_EXPORT_XML);
        final String exportFilePath = expFile.getAbsolutePath();
        try {
            ContentModelExporter.exportContentModelToXmlFile(exportFilePath, MODEL, "../org.csstudio.utility.treemodel/testres/test.dtd");
        } catch (final ExportContentModelException e) {
            Assert.fail("XML file export exception: " + e.getMessage());
        }

        // Get DOM tree of exported test file via JDOM
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(exportFilePath);
            final DataInputStream in = new DataInputStream(fstream);
            final SAXBuilder saxBuilder = new SAXBuilder(true);
            _exportedDoc = saxBuilder.build(in);
        } catch (final FileNotFoundException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        } catch (final JDOMException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        } catch (final IOException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        }

        // Compare the input file and the output file
        try {
            final String impStr = _outputter.outputString(IMPORTED_DOC.getRootElement());
            final String expStr = _outputter.outputString(_exportedDoc.getRootElement());

            final Diff diff = new Diff(impStr, expStr);
            diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());

            Assert.assertTrue(diff.similar());

            Assert.assertTrue("Deletion of exported file failed.", expFile.delete());

        } catch (final SAXException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        } catch (final IOException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        }
    }
}
