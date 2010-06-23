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
import java.util.List;

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
public class TopicSetColumnService {

	private final List<TopicSet> _topicSets = new ArrayList<TopicSet>();
	private final List<String[]> _columnSets = new ArrayList<String[]>();

	public TopicSetColumnService(final String topicSetPreferenceKey, final String columnSetPreferenceKey) {
		IPreferenceStore store = JmsLogsPlugin.getDefault()
		.getPreferenceStore();
		parseTopicSetString(store.getString(topicSetPreferenceKey));
		parseColumnSetString(store.getString(columnSetPreferenceKey));
	}

	private void parseColumnSetString(final String columnSetString) {
//		CentralLogger.getInstance().debug(this, "Column Pref String: " + columnSetString);
		String[] columnSets = columnSetString.split("\\?");
		for (String columnSet : columnSets) {
			String[] columnItems = columnSet.split(";");
			_columnSets.add(columnItems);
		}
	}

	private void parseTopicSetString(final String topicSetString) {
		String[] topicSetsAndNames = topicSetString.split(";"); //$NON-NLS-1$
		for (String topicSet : topicSetsAndNames) {
			String[] topicSetItems = Arrays.copyOf(topicSet.split("\\?"), 6);
			_topicSets.add(new TopicSet(topicSetItems[0], topicSetItems[1], topicSetItems[2], topicSetItems[3], topicSetItems[4], topicSetItems[5]));
		}
	}

	/**
	 * If there is no
	 * default the first item is taken. The default tag from the preferences is
	 * overwritten if there is a topic set from a previous session.
	 *
	 * @param topics
	 *            raw topic string from preferences
	 * @return set of topics for initialization
	 */
	public String getDefaultTopicSet() {
		for (TopicSet topicSet : _topicSets) {
			if(topicSet.isDefaultTopic()) {
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
	 * Get the font for the given topic set name.
	 */
	public Font getFont(final String topicSetName) {
		int i = getTopicSetIndex(topicSetName);
		TopicSet topicSet = _topicSets.get(i);
		return topicSet.getFont();
	}

	/**
	 * Get corresponding columnSet for the given topicSet
	 *
	 * @param defaultTopicSet
	 * @return
	 */
	public String[] getColumnSet(final String topicSet) {
		int i = getTopicSetIndex(topicSet);
		String[] columns;
		try {
			columns = _columnSets.get(i);
		} catch (IndexOutOfBoundsException e)	 {
			//There are no corresponding column settings, get first column set
			columns = _columnSets.get(0);
		}
		return columns;
	}

	private int getTopicSetIndex(final String topicSet) {
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

	public List<TopicSet> getTopicSets() {
		return _topicSets;
	}

	/**
	 * Read the JMS Topics for the given topicSetName.
	 *
	 * @param currentTopicSet
	 * @return
	 */
	public TopicSet getJMSTopics(final String currentTopicSet) {
		for (TopicSet topicSetTmp : _topicSets) {
			if (topicSetTmp.getName().equals(currentTopicSet)) {
				return topicSetTmp;
			}
		}
		return _topicSets.get(0);
	}
}
