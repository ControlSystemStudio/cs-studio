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
package org.csstudio.utility.treemodel.builder;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import junit.framework.Assert;

import org.csstudio.utility.treemodel.Activator;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.eclipse.core.runtime.FileLocator;
import org.jdom.input.JDOMParseException;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * Test content model class and builder from XML.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 21.05.2010
 */
public class XmlFileContentModelBuilderTest {

    private static final String TEST_VALID_XML = "Test_Valid.xml";
    private static final String TEST_EMPTY_XML = "Test_Empty.xml";
    private static final String TEST_INVALID_XML = "Test_InvalidStructure.xml";

    private static File RES_PATH;

    private ContentModel<TestTreeConfigurator> _model;


    @BeforeClass
    public static final void buildResourcePath() {
        try {
            final File loc = FileLocator.getBundleFile(Activator.getDefault().getBundle());

            final File testPath = new File(loc, "test");
            RES_PATH = new File(testPath, "res");
        } catch (final IOException e1) {
            Assert.fail("File locator could not deliver file");
        }
    }

    @Test
    public void testValid() {
        final XmlFileContentModelBuilder<TestTreeConfigurator> builder =
            new XmlFileContentModelBuilder<TestTreeConfigurator>(TestTreeConfigurator.ROOT,
                                                                 new File(RES_PATH, TEST_VALID_XML).getAbsolutePath());
        try {
            builder.build();
            _model = builder.getModel();
        } catch (final CreateContentModelException e) {
            Assert.fail(e.getMessage() + "\n" + e.getCause());
        }

        testSimpleNamesCache();
    }


    private void testSimpleNamesCache() {
        Set<String> simpleNames = _model.getSimpleNames(TestTreeConfigurator.FACILITY);
        Assert.assertEquals(simpleNames.size(), 2);
        simpleNames = _model.getSimpleNames(TestTreeConfigurator.COMPONENT);
        Assert.assertEquals(simpleNames.size(), 3);
        simpleNames = _model.getSimpleNames(TestTreeConfigurator.IOC);
        Assert.assertEquals(simpleNames.size(), 5);
        simpleNames = _model.getSimpleNames(TestTreeConfigurator.RECORD);
        Assert.assertEquals(simpleNames.size(), 17);
    }

    @Test
    public void testEmpty() {
        final String xmlFilePath = new File(RES_PATH, TEST_EMPTY_XML).getAbsolutePath();

        final XmlFileContentModelBuilder<TestTreeConfigurator> builder =
            new XmlFileContentModelBuilder<TestTreeConfigurator>(TestTreeConfigurator.ROOT,
                    xmlFilePath);
        try {
            builder.build();
        } catch (final CreateContentModelException e) {
            System.out.println(e.getMessage());
            Assert.assertTrue((e.getCause() instanceof JDOMParseException));
            Assert.assertEquals("File " + xmlFilePath + " contains parsing errors. Premature end of file.", e.getMessage());
            return;
        }
        Assert.fail("No exceptions?");
    }

    @Test
    public void testInvalidXML() {
        final String xmlFilePath = new File(RES_PATH, TEST_INVALID_XML).getAbsolutePath();

        final XmlFileContentModelBuilder<TestTreeConfigurator> builder =
            new XmlFileContentModelBuilder<TestTreeConfigurator>(TestTreeConfigurator.ROOT,
                    xmlFilePath);
        try {
            builder.build();
        } catch (final CreateContentModelException e) {
            Assert.assertTrue((e.getCause() instanceof JDOMParseException));
            Assert.assertEquals("File " + xmlFilePath + " contains parsing errors. Element type \"ecock\" must be declared.", e.getMessage());
            return;
        }
        Assert.fail("No exceptions?");
    }
}
