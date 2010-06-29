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
    public static final String ITEM_SEPARATOR = ";";
    public static final String INNER_ITEM_SEPARATOR_AS_REGEX = "\\?";
    public static final String INNER_ITEM_SEPARATOR = "?";

    private final List<TopicSet> _topicSets = new ArrayList<TopicSet>();
    private final List<String[]> _columnSets = new ArrayList<String[]>();
    private final List<ColumnDescription> _columnDescriptions;

    public TopicSetColumnService(@Nonnull final String topicSetPreferenceKey,
                                 @Nonnull final String columnSetPreferenceKey,
                                 @Nonnull final List<ColumnDescription> columnDescriptions) {
        _columnDescriptions = new ArrayList<ColumnDescription>(columnDescriptions);
        IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
        parseTopicSetString(store.getString(topicSetPreferenceKey));
        parseColumnSetString(store.getString(columnSetPreferenceKey));
    }

    private void parseColumnSetString(@Nonnull final String columnSetString) {
        //		CentralLogger.getInstance().debug(this, "Column Pref String: " + columnSetString);
        String[] columnSets = columnSetString.split(INNER_ITEM_SEPARATOR_AS_REGEX);
        for (String columnSet : columnSets) {
            String[] columnItems = columnSet.split(ITEM_SEPARATOR);
            _columnSets.add(columnItems);
        }
    }

    private void parseTopicSetString(@Nonnull final String topicSetString) {
        String[] topicSetsAsString = topicSetString.split(ITEM_SEPARATOR); //$NON-NLS-1$
        for (String topicSetAsString : topicSetsAsString) {
            String[] topicSetItems = Arrays
                    .copyOf(topicSetAsString.split(INNER_ITEM_SEPARATOR_AS_REGEX), ColumnDescription.values().length);
            // TODO (jpenning) Add initial state to topic set
            TopicSet topicSet = new TopicSet.Builder().setDefaultTopic(topicSetItems[0])
                    .setTopics(topicSetItems[1]).setName(topicSetItems[2])
                    .setPopUp(topicSetItems[3]).setStartUp(topicSetItems[4])
                    .setFont(topicSetItems[5]).build();

            _topicSets.add(topicSet);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public String getDefaultTopicSet() {
        for (TopicSet topicSet : _topicSets) {
            if (topicSet.isDefaultTopic()) {
                return topicSet.getName();
            }
        }
        if (_topicSets.size() > 0) {
            return _topicSets.get(0).getName();
        } else {
            return "";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public Font getFont(@Nonnull final String topicSetName) {
        int i = getTopicSetIndex(topicSetName);
        TopicSet topicSet = _topicSets.get(i);
        return topicSet.getFont();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public String[] getColumnSet(@Nonnull final String topicSet) {
        int i = getTopicSetIndex(topicSet);
        String[] columns;
        try {
            columns = _columnSets.get(i);
        } catch (IndexOutOfBoundsException e) {
            //There are no corresponding column settings, get first column set
            columns = _columnSets.get(0);
        }
        return columns;
    }

    private int getTopicSetIndex(@Nonnull final String topicSet) {
        //get index of given topic set
        int i = 0;
        for (TopicSet topicSetTmp : _topicSets) {
            if (topicSetTmp.getName().equals(topicSet)) {
                break;
            }
            i++;
        }
        return i;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public List<TopicSet> getTopicSets() {
        return _topicSets;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public TopicSet getJMSTopics(@Nonnull final String currentTopicSet) {
        for (TopicSet topicSetTmp : _topicSets) {
            if (topicSetTmp.getName().equals(currentTopicSet)) {
                return topicSetTmp;
            }
        }
        return _topicSets.get(0);
    }

    @Override
    public List<ColumnDescription> getColumnDescriptions() {
        return Collections.unmodifiableList(_columnDescriptions);
    }

    private int getColumnCount() {
        return _columnDescriptions.size();
    }

}
