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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import static org.mockito.Mockito.*;

/**
 * Test of topic set column service
 * 
 * The tests for the different versions of the preference string must be kept because they are present in certain installations and ensure
 * downward compatibility.
 *
 * @author jpenning
 * @since 12.3.2012
 */
public class TopicSetColumnServiceUnitTest {
    private final static String TOPICSET_PREFERENCE_KEY = "topicSetPreferenceKey";
    private final static String COLUMNSET_PREFERENCE_KEY = "columnSetPreferenceKey";
    
    private IPreferenceStore _preferenceStore = mock(IPreferenceStore.class);
    private TopicSetColumnService _serviceUnderTest = null;
    
    @Before
    public void setUp() {
        List<ColumnDescription> columnDescriptions = newColumnDescriptions();
        _serviceUnderTest = new TopicSetColumnService(TOPICSET_PREFERENCE_KEY,
                                                      COLUMNSET_PREFERENCE_KEY,
                                                      columnDescriptions,
                                                      _preferenceStore);
    }
    
    @Test
    public void testColumnDescription() {
        List<ColumnDescription> retrievedColumnDescriptions = _serviceUnderTest
                .getColumnDescriptions();
        assertThat(retrievedColumnDescriptions,
                   hasItems(ColumnDescription.IS_DEFAULT_ENTRY, ColumnDescription.TOPIC_SET));
    }
    
    @Test
    public void testColumnSet() {
        // this form of the preference string is very simple, it contains only the data for this test, other data is omitted
        when(_preferenceStore.getString(TOPICSET_PREFERENCE_KEY))
                .thenReturn("??MyName;??OtherName");
        
        when(_preferenceStore.getString(COLUMNSET_PREFERENCE_KEY))
                .thenReturn("ACK,57;TYPE,147;?ACK,49;TYPE,147;HOST,155;");
        
        String[] columnsStrings = _serviceUnderTest.getColumnSet("MyName");
        assertEquals(2, columnsStrings.length);
        assertEquals("ACK,57", columnsStrings[0]);
        assertEquals("TYPE,147", columnsStrings[1]);
        
        columnsStrings = _serviceUnderTest.getColumnSet("OtherName");
        assertEquals(3, columnsStrings.length);
        assertEquals("ACK,49", columnsStrings[0]);
        assertEquals("HOST,155", columnsStrings[2]);
        
    }
    
    @Test
    public void testTopicSetVersion1() {
        // the first version had no initial state flag at the end
        when(_preferenceStore.getString(TOPICSET_PREFERENCE_KEY))
                .thenReturn("default?A,B?MyName?false?true?MyFont;");
        
        List<TopicSet> retrievedTopicSets = _serviceUnderTest.getTopicSets();
        assertEquals(1, retrievedTopicSets.size());
        
        TopicSet topicSet = retrievedTopicSets.get(0);
        assertTrue(topicSet.isDefaultTopic());
        assertThat(topicSet.getTopics(), hasItems("A", "B"));
        assertEquals("MyName", topicSet.getName());
        assertFalse(topicSet.isPopUp());
        assertTrue(topicSet.isStartUp());
        assertFalse(topicSet.isRetrieveInitialState());
        assertFalse(topicSet.isSynchedToTree());
    }
    
    @Test
    public void testTopicSetVersion2() {
        // the 2nd version has a flag added at the end to indicate the initial retrieval of the alarm state
        when(_preferenceStore.getString(TOPICSET_PREFERENCE_KEY))
                .thenReturn("default?A,B?MyName?false?true?MyFont?false;?C,D,E?OtherName?true?false?OtherFont?true;");
        
        List<TopicSet> retrievedTopicSets = _serviceUnderTest.getTopicSets();
        assertEquals(2, retrievedTopicSets.size());
        
        TopicSet topicSet = retrievedTopicSets.get(0);
        assertTrue(topicSet.isDefaultTopic());
        assertThat(topicSet.getTopics(), hasItems("A", "B"));
        assertEquals("MyName", topicSet.getName());
        assertFalse(topicSet.isPopUp());
        assertTrue(topicSet.isStartUp());
        assertFalse(topicSet.isRetrieveInitialState());
        assertFalse(topicSet.isSynchedToTree());

        topicSet = retrievedTopicSets.get(1);
        assertFalse(topicSet.isDefaultTopic());
        assertThat(topicSet.getTopics(), hasItems("C", "D", "E"));
        assertEquals("OtherName", topicSet.getName());
        assertTrue(topicSet.isPopUp());
        assertFalse(topicSet.isStartUp());
        assertTrue(topicSet.isRetrieveInitialState());
        assertFalse(topicSet.isSynchedToTree());

        assertEquals("MyName", _serviceUnderTest.getDefaultTopicSet());
    }
    
    @Test
    public void testTopicSetVersion3() {
        // the 3rd version has a flag added at the end to indicate if the current entry should be in sync with the alarm tree.
        when(_preferenceStore.getString(TOPICSET_PREFERENCE_KEY))
        .thenReturn("default?A,B?MyName?false?true?MyFont?false?true;?C,D,E?OtherName?true?false?OtherFont?true?false;");
        
        List<TopicSet> retrievedTopicSets = _serviceUnderTest.getTopicSets();
        assertEquals(2, retrievedTopicSets.size());
        
        TopicSet topicSet = retrievedTopicSets.get(0);
        assertTrue(topicSet.isDefaultTopic());
        assertThat(topicSet.getTopics(), hasItems("A", "B"));
        assertEquals("MyName", topicSet.getName());
        assertFalse(topicSet.isPopUp());
        assertTrue(topicSet.isStartUp());
        assertFalse(topicSet.isRetrieveInitialState());
        assertTrue(topicSet.isSynchedToTree());
        
        topicSet = retrievedTopicSets.get(1);
        assertFalse(topicSet.isDefaultTopic());
        assertThat(topicSet.getTopics(), hasItems("C", "D", "E"));
        assertEquals("OtherName", topicSet.getName());
        assertTrue(topicSet.isPopUp());
        assertFalse(topicSet.isStartUp());
        assertTrue(topicSet.isRetrieveInitialState());
        assertFalse(topicSet.isSynchedToTree());
        
        assertEquals("MyName", _serviceUnderTest.getDefaultTopicSet());
    }
    
    @Test
    public void testGetTopicSetByName() {
        // this form of the preference string is very simple, it contains only the data for this test, other data is omitted
        when(_preferenceStore.getString(TOPICSET_PREFERENCE_KEY))
                .thenReturn("??MyName;??OtherName");

        TopicSet topicSet = _serviceUnderTest.getTopicSetByName("MyName");
        assertEquals("MyName", topicSet.getName());
        topicSet = _serviceUnderTest.getTopicSetByName("OtherName");
        assertEquals("OtherName", topicSet.getName());
        
        // and now the ugly part: if no topic set by that name is known, the first in the list is returned
        // it is not cared for if there is none in the list
        // this should be changed
        topicSet = _serviceUnderTest.getTopicSetByName("Unknown");
        assertEquals("MyName", topicSet.getName());
        
    }

    @Nonnull
    private List<ColumnDescription> newColumnDescriptions() {
        final List<ColumnDescription> result = new ArrayList<ColumnDescription>();
        result.add(ColumnDescription.IS_DEFAULT_ENTRY);
        result.add(ColumnDescription.TOPIC_SET);
        //        result.add(ColumnDescription.NAME_FOR_TOPIC_SET);
        //        result.add(ColumnDescription.AUTO_START);
        //        result.add(ColumnDescription.RETRIEVE_INITIAL_STATE);
        //        result.add(ColumnDescription.FONT);
        return result;
    }
    
}
