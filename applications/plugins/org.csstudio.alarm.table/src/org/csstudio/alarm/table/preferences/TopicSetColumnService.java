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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to handle the relation between topic sets and column sets.
 * For each TopicSet a ColumnSet defines the set and width of table columns.
 * Because the number of topicSets is dynamic all topic sets are stored
 * in one preference string. The related columnSets are also stored in one
 * preference string.
 *
 * @author jhatje
 *
 */
public class TopicSetColumnService implements ITopicSetColumnService {

    private static final Logger LOG = LoggerFactory.getLogger(TopicSetColumnService.class);
    
    public static final String ITEM_SEPARATOR = ";";
    public static final String INNER_ITEM_SEPARATOR_AS_REGEX = "\\?";
    public static final String INNER_ITEM_SEPARATOR = "?";

    private final List<ColumnDescription> _columnDescriptions;

    private final String _topicSetPreferenceKey;
    private final String _columnSetPreferenceKey;

    public TopicSetColumnService(@Nonnull final String topicSetPreferenceKey,
                                 @Nonnull final String columnSetPreferenceKey,
                                 @Nonnull final List<ColumnDescription> columnDescriptions) {
        _topicSetPreferenceKey = topicSetPreferenceKey;
        _columnSetPreferenceKey = columnSetPreferenceKey;

        _columnDescriptions = new ArrayList<ColumnDescription>(columnDescriptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDefaultTopicSet() {
        List<TopicSet> topicSets = readTopicSet();
        for (TopicSet topicSet : topicSets) {
            if (topicSet.isDefaultTopic()) {
                return topicSet.getName();
            }
        }
        if (topicSets.size() > 0) {
            return topicSets.get(0).getName();
        } else {
            return "";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Font getFont(@Nonnull final String topicSetName) {
        int i = getTopicSetIndex(topicSetName);
        TopicSet topicSet = readTopicSet().get(i);
        return topicSet.getFont();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String[] getColumnSet(@Nonnull final String topicSet) {
        List<String[]> columnSets = readColumnSets();
        int i = getTopicSetIndex(topicSet);
        String[] columns;
        try {
            columns = columnSets.get(i);
        } catch (IndexOutOfBoundsException e) {
            //There are no corresponding column settings, get first column set
            columns = columnSets.get(0);
        }
        return columns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<TopicSet> getTopicSets() {
        return readTopicSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public TopicSet getJMSTopics(@Nonnull final String currentTopicSet) {
        List<TopicSet> topicSets = readTopicSet();
        for (TopicSet topicSetTmp : topicSets) {
            if (topicSetTmp.getName().equals(currentTopicSet)) {
                return topicSetTmp;
            }
        }
        return topicSets.get(0);
    }

    @Override
    public List<ColumnDescription> getColumnDescriptions() {
        return Collections.unmodifiableList(_columnDescriptions);
    }

    // get index of given topic set
    private int getTopicSetIndex(@Nonnull final String topicSet) {
        int i = 0;
        for (TopicSet topicSetTmp : readTopicSet()) {
            if (topicSetTmp.getName().equals(topicSet)) {
                break;
            }
            i++;
        }
        return i;
    }


    @Nonnull
    private List<String[]> readColumnSets() {
        IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
        return createColumnSetsFromPreference(store.getString(_columnSetPreferenceKey));
    }

    @Nonnull
    private List<String[]> createColumnSetsFromPreference(@Nonnull final String columnSetString) {
        LOG.debug("Column Pref String: {}", columnSetString);

        List<String[]> result = new ArrayList<String[]>();
        String[] columnSets = columnSetString.split(INNER_ITEM_SEPARATOR_AS_REGEX);
        for (String columnSet : columnSets) {
            String[] columnItems = columnSet.split(ITEM_SEPARATOR);
            result.add(columnItems);
        }
        return result;
    }

    @Nonnull
    private List<TopicSet> readTopicSet() {
        IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
        return readTopicSetsFromPreference(store.getString(_topicSetPreferenceKey));
    }

    @Nonnull
    private List<TopicSet> readTopicSetsFromPreference(@Nonnull final String topicSetString) {
        List<TopicSet> result = new ArrayList<TopicSet>();
        String[] topicSetsAsString = topicSetString.split(ITEM_SEPARATOR); //$NON-NLS-1$
        for (String topicSetAsString : topicSetsAsString) {
            TopicSet topicSet = createTopicSetFromString(topicSetAsString);
            result.add(topicSet);
        }
        return result;
    }

    @Nonnull
    private TopicSet createTopicSetFromString(@Nonnull String topicSetAsString) {
        String[] topicSetItems = Arrays.copyOf(topicSetAsString
                .split(INNER_ITEM_SEPARATOR_AS_REGEX), ColumnDescription.values().length);
        // Here - and hopefully only here - the order of the inner items of a preferences string is defined
        TopicSet topicSet = new TopicSet.Builder().setDefaultTopic(topicSetItems[0])
                .setTopics(topicSetItems[1]).setName(topicSetItems[2])
                .setPopUp(topicSetItems[3]).setStartUp(topicSetItems[4])
                .setFont(topicSetItems[5]).setRetrieveInitialState(topicSetItems[6]).build();
        return topicSet;
    }

}
