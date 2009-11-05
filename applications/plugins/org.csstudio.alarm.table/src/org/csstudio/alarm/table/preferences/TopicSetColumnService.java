package org.csstudio.alarm.table.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

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

	private List<TopicSet> _topicSets = new ArrayList<TopicSet>();
	private List<String[]> _columnSets = new ArrayList<String[]>();

	public TopicSetColumnService(String topicSetPreferenceKey, String columnSetPreferenceKey) {
		IPreferenceStore store = JmsLogsPlugin.getDefault()
		.getPreferenceStore();
		parseTopicSetString(store.getString(topicSetPreferenceKey));
		parseColumnSetString(store.getString(columnSetPreferenceKey));
	}

	private void parseColumnSetString(String columnSetString) {
//		CentralLogger.getInstance().debug(this, "Column Pref String: " + columnSetString);
		String[] columnSets = columnSetString.split("\\?");
		for (String columnSet : columnSets) {
			String[] columnItems = columnSet.split(";");
			_columnSets.add(columnItems);
		}
	}

	private void parseTopicSetString(String topicSetString) {
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
	public Font getFont(String topicSetName) {
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
	public String[] getColumnSet(String topicSet) {
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

	private int getTopicSetIndex(String topicSet) {
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
	public List<String> getJMSTopics(String currentTopicSet) {
		for (TopicSet topicSetTmp : _topicSets) {
			if (topicSetTmp.getName().equals(currentTopicSet)) {
				return topicSetTmp.getTopics();
			}
		}
		return _topicSets.get(0).getTopics();
	}
}
