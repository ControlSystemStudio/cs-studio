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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;


import junit.framework.Assert;

import org.csstudio.utility.treemodel.builder.TestTreeConfiguration;
import org.csstudio.utility.treemodel.builder.XmlFileContentModelBuilderHeadlessTest;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
public class ContentModelExporterIT {

    private static final String TEST_EXPORT_XML = "Test_Export.xml";
    private static final String TEST_DTD = "test.dtd";

    private static ContentModel<TestTreeConfiguration> MODEL;
    private static Document IMPORTED_DOC;
    private Document _exportedDoc;

    private final Format f = Format.getPrettyFormat();
    private final XMLOutputter _outputter = new XMLOutputter(f);


    @BeforeClass
    public static final void buildResourcePath() {

        final URL resource = TreeModelTestUtils.findResource(XmlFileContentModelBuilderHeadlessTest.TEST_VALID_XML);
        Assert.assertNotNull(resource);

        try {
            MODEL = TreeModelTestUtils.buildContentModel(resource, TestTreeConfiguration.VIRTUAL_ROOT);
        } catch (final CreateContentModelException e) {
            Assert.fail("Content model could not be created. " + e.getLocalizedMessage());
        } catch (final IOException e) {
            Assert.fail("Resource could not be opened. " + e.getLocalizedMessage());
        }

        IMPORTED_DOC = getDomTreeOfResource(resource);
    }

    private static Document getDomTreeOfResource(final URL resource) {
        InputStream stream = null;
        Document doc = null;
        // Get DOM tree of imported test file via JDOM
        try {
            stream = resource.openStream();
            final DataInputStream in = new DataInputStream(stream);
            final SAXBuilder saxBuilder = new SAXBuilder(true);
            doc = saxBuilder.build(in);
        } catch (final FileNotFoundException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        } catch (final JDOMException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        } catch (final IOException e) {
            Assert.fail("Unexpected exception: " + e.getMessage() + "\n" + e.getCause());
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (final IOException e) {
                Assert.fail("Unexpected exception closing input stream: " + e.getMessage() + "\n" + e.getCause());
            }
        }
        return doc;
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testExportContentModelToFile() throws IOException {

        final Bundle bundle = Platform.getBundle(TreeModelActivator.PLUGIN_ID);
        Assert.assertNotNull("Bundle " + TreeModelActivator.PLUGIN_ID + " is null!", bundle);
        final String fileSeparator = "/";
        final Enumeration<URL> xmlEntries = bundle.findEntries(fileSeparator, "*.xml", true);
        Assert.assertTrue("No xml entries found!", xmlEntries.hasMoreElements());

        final URL entry = xmlEntries.nextElement();


        final String fullPath = FileLocator.toFileURL(entry).getPath();
        final int lastIndexOf = fullPath.lastIndexOf(fileSeparator);
        final String basePath = fullPath.substring(0, lastIndexOf);


        try {
            ContentModelExporter.exportContentModelToXmlFile(basePath + fileSeparator +  TEST_EXPORT_XML, MODEL,
                                                             basePath + fileSeparator + TEST_DTD);
        } catch (final ExportContentModelException e) {
            Assert.fail("XML file export exception: " + e.getMessage());
        }

        final File expFile = new File(basePath, TEST_EXPORT_XML);
        Assert.assertTrue(expFile.exists());

        _exportedDoc = getDomTreeOfResource(expFile.toURI().toURL());

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
