/**
 * 
 */
package org.csstudio.channel.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Helper class which return items which can be displayed in a jface table.
 * 
 * @author shroffk
 * 
 */
public class TunerSetpointTableModel {

	private List<Map<String, Double>> calculatedSetpoints;
	private Set<TunerSetpointTableModelListener> listeners = new HashSet<TunerSetpointTableModelListener>();

	public void addSetpointTableModelListener(
			TunerSetpointTableModelListener listener) {
		listeners.add(listener);
	}

	public void removeSetpointTableModelListener(
			TunerSetpointTableModelListener listener) {
		listeners.remove(listener);
	}

	private void firesetpointsChanged() {
		for (TunerSetpointTableModelListener listener : listeners) {
			listener.setpointsChanged();
		}
	}

	public TunerSetpointTableModel(List<Map<String, Double>> calculatedSetpoints) {
		if (calculatedSetpoints != null) {
			this.calculatedSetpoints = calculatedSetpoints;
		} else {
			throw new IllegalArgumentException(
					"calculatedSetpoints cannot be null.");
		}
	}

	public void setCalculatedSetpoints(
			List<Map<String, Double>> calculatedSetpoints) {
		this.calculatedSetpoints = calculatedSetpoints;
		firesetpointsChanged();
	}

	public List<Map<String, Double>> getCalculatedSetpoints() {
		return Collections.unmodifiableList(calculatedSetpoints);
	}

	public void setCalculatedSetpoint(int columnIndex, String rowIdentifier,
			Double value) {
		if (this.calculatedSetpoints != null) {
			if (this.calculatedSetpoints.get(columnIndex).containsKey(
					rowIdentifier)) {
				this.calculatedSetpoints.get(columnIndex).put(rowIdentifier,
						value);
				// TODO
				// firesetpointsChanged();
			}
		}
	}

	public Map<String, Double> getNextSetpoints() {
		if (calculatedSetpoints.size() > 0) {
			Map<String, Double> setPoints = Collections
					.unmodifiableMap(calculatedSetpoints.get(0));
			calculatedSetpoints.remove(0);
			firesetpointsChanged();
			return setPoints;
		}
		return null;
	}

	public int getNumberOfSteps() {
		return calculatedSetpoints.size();
	}

	class TableItem {
		final int row;
		final String channelName;

		private TableItem(int row, String channelName) {
			this.row = row;
			this.channelName = channelName;
		}

		public List<Double> getValue() {
			List<Double> values = new ArrayList<Double>(
					calculatedSetpoints.size());
			for (Map<String, Double> map : calculatedSetpoints) {
				values.add(map.get(channelName));
			}
			return values;
		}

	}

	public TableItem[] getItems() {
		if (this.calculatedSetpoints != null) {
			int columnCount = calculatedSetpoints.get(0).size();
			TableItem[] result = new TableItem[columnCount];

			Map<String, Double> tst = calculatedSetpoints.get(0);
			int i = 0;
			for (String key : tst.keySet()) {
				result[i] = new TableItem(i, key);
				i++;
			}
			return result;
		}
		return null;
	}

}
