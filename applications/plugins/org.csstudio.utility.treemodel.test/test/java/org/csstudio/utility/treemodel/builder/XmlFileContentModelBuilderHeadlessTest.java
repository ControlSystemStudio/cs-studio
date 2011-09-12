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

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import junit.framework.Assert;

import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.TreeModelTestUtils;
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
public class XmlFileContentModelBuilderHeadlessTest {

    public static final String TEST_VALID_XML = "res-test/Test_Valid.xml";
    private static URL RESOURCE_VALID;
    private static URL RESOURCE_INVALID;
    private static URL RESOURCE_EMPTY;
    private static final String TEST_EMPTY_XML = "res-test/Test_Empty.xml";
    private static final String TEST_INVALID_XML = "res-test/Test_InvalidStructure.xml";

    private ContentModel<TestTreeConfiguration> _model;

    @BeforeClass
    public static void buildResourcePath() {
        RESOURCE_VALID = TreeModelTestUtils.findResource(TEST_VALID_XML);
        Assert.assertNotNull(RESOURCE_VALID);
        RESOURCE_INVALID = TreeModelTestUtils.findResource(TEST_INVALID_XML);
        Assert.assertNotNull(RESOURCE_INVALID);
        RESOURCE_EMPTY = TreeModelTestUtils.findResource(TEST_EMPTY_XML);
        Assert.assertNotNull(RESOURCE_EMPTY);
    }

    @Test
    public void testValid() {
        try {
            _model = TreeModelTestUtils.buildContentModel(RESOURCE_VALID,
                                                          TestTreeConfiguration.VIRTUAL_ROOT);
        } catch (final CreateContentModelException e) {
            Assert.fail("Content model could not be created. " + e.getLocalizedMessage());
        } catch (final IOException e) {
            Assert.fail("Resource could not be opened. " + e.getLocalizedMessage());
        }

        testSimpleNamesCache();
    }


    private void testSimpleNamesCache() {
        Set<String> simpleNames = _model.getSimpleNames(TestTreeConfiguration.FACILITY);
        Assert.assertEquals(simpleNames.size(), 2);
        simpleNames = _model.getSimpleNames(TestTreeConfiguration.COMPONENT);
        Assert.assertEquals(simpleNames.size(), 3);
        simpleNames = _model.getSimpleNames(TestTreeConfiguration.IOC);
        Assert.assertEquals(simpleNames.size(), 5);
        simpleNames = _model.getSimpleNames(TestTreeConfiguration.RECORD);
        Assert.assertEquals(simpleNames.size(), 17);
    }

    @Test
    public void testEmpty() {
        try {
            TreeModelTestUtils.buildContentModel(RESOURCE_EMPTY, TestTreeConfiguration.UNIT);
        } catch (final CreateContentModelException e) {
            Assert.assertTrue((e.getCause() instanceof JDOMParseException));
            Assert.assertEquals("File contains parsing errors. Premature end of file.", e.getMessage());
            return;
        } catch (final Exception e) {
            Assert.fail("Wrong exception. " + e.getMessage() + "\n" + e.getCause());
        }
        Assert.fail("No exceptions?");
    }

    @Test
    public void testInvalidXML() {
        try {
            TreeModelTestUtils.buildContentModel(RESOURCE_INVALID, TestTreeConfiguration.UNIT);
        } catch (final CreateContentModelException e) {
            Assert.assertTrue((e.getCause() instanceof JDOMParseException));
            Assert.assertEquals("File contains parsing errors. Element type \"ecock\" must be declared.", e.getMessage());
            return;
        } catch (final Exception e) {
            Assert.fail("Wrong exception. " + e.getMessage() + "\n" + e.getCause());
        }
        Assert.fail("No exceptions?");
    }
}
