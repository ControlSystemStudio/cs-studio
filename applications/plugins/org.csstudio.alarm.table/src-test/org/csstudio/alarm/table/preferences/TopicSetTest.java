/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test of topic set
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 24.06.2010
 */
public class TopicSetTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDefault() {
        TopicSet topicSet = new TopicSet.Builder().build();
        assertFalse(topicSet.isDefaultTopic());

        topicSet = new TopicSet.Builder().setDefaultTopic("default").build();
        assertTrue(topicSet.isDefaultTopic());
    }

    @Test
    public void testListOfTopics() {
        TopicSet topicSet = new TopicSet.Builder().build();
        assertEquals(null, topicSet.getTopics());

        topicSet = new TopicSet.Builder().setTopics("").build();
        assertEquals(1, topicSet.getTopics().size());
        assertEquals("", topicSet.getTopics().get(0));

        topicSet = new TopicSet.Builder().setTopics("t1").build();
        assertEquals(1, topicSet.getTopics().size());
        assertEquals("t1", topicSet.getTopics().get(0));

        topicSet = new TopicSet.Builder().setTopics("t1,t2").build();
        assertEquals(2, topicSet.getTopics().size());
        assertEquals("t1", topicSet.getTopics().get(0));
        assertEquals("t2", topicSet.getTopics().get(1));
    }

    @Test
    public void testName() {
        TopicSet topicSet = new TopicSet.Builder().build();
        assertEquals("not set", topicSet.getName());

        topicSet = new TopicSet.Builder().setName("testname").build();
        assertEquals("testname", topicSet.getName());
    }


    @Test
    public void testPopUp() {
        TopicSet topicSet = new TopicSet.Builder().build();
        assertFalse(topicSet.isPopUp());

        topicSet = new TopicSet.Builder().setPopUp("false").build();
        assertFalse(topicSet.isPopUp());

        topicSet = new TopicSet.Builder().setPopUp("true").build();
        assertTrue(topicSet.isPopUp());
    }

    @Test
    public void testStartUp() {
        TopicSet topicSet = new TopicSet.Builder().build();
        assertFalse(topicSet.isStartUp());

        topicSet = new TopicSet.Builder().setStartUp("false").build();
        assertFalse(topicSet.isStartUp());

        topicSet = new TopicSet.Builder().setStartUp("true").build();
        assertTrue(topicSet.isStartUp());
    }

    @Test
    @Ignore("can only be run as plugin test")
    public void testFont() {
        TopicSet topicSet = new TopicSet.Builder().build();
        assertEquals(null, topicSet.getFont());

        topicSet = new TopicSet.Builder().setFont("Tahoma,0,8").build();
        assertEquals(1, topicSet.getFont().getFontData().length);
    }

    @Test
    public void testRetrieveInitialState() {
        TopicSet topicSet = new TopicSet.Builder().build();
        assertFalse(topicSet.isRetrieveInitialState());

        topicSet = new TopicSet.Builder().setRetrieveInitialState("false").build();
        assertFalse(topicSet.isRetrieveInitialState());

        topicSet = new TopicSet.Builder().setRetrieveInitialState("true").build();
        assertTrue(topicSet.isRetrieveInitialState());
    }


}
