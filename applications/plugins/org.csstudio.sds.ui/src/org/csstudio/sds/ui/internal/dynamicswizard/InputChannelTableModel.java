package org.csstudio.sds.ui.internal.dynamicswizard;

import java.util.ArrayList;
import java.util.List;

public class InputChannelTableModel {
	private List<InputChannelTableRow> _rowsForInputChannels;
	
	private List<InputChannelTableRow> _rowsForOutputChannels;

	public InputChannelTableModel() {
		_rowsForInputChannels = new ArrayList<InputChannelTableRow>();
		_rowsForOutputChannels = new ArrayList<InputChannelTableRow>();
	}

	public void addRowForInputChannel(InputChannelTableRow row) {
		_rowsForInputChannels.add(row);
	}
	
	public void addRowForOutputChannel(InputChannelTableRow row) {
		_rowsForOutputChannels.add(row);
	}
	
	public void removeRow(InputChannelTableRow row) {
		_rowsForInputChannels.remove(row);
		_rowsForOutputChannels.remove(row);
	}
	
	public void clearInputChannelDescriptions() {
		for(InputChannelTableRow row : _rowsForInputChannels) {
			row.setDescription("");
			row.setDefaultValue(null);
			row.setValueType(Object.class);
		}
	}

	public List<InputChannelTableRow> getAllRows() {
		List<InputChannelTableRow> result = new ArrayList<InputChannelTableRow>();
		result.addAll(_rowsForInputChannels);
		result.addAll(_rowsForOutputChannels);
		return result;
	}

	@SuppressWarnings("unchecked")
	public void setInputChannelDescription(final int rowIndex, final String description, final Class clazz) {
		if(rowIndex >= _rowsForInputChannels.size()) {
			int addCount  = _rowsForInputChannels.size()-rowIndex+1;
			for(int i=0;i<addCount;i++) {
				_rowsForInputChannels.add(new InputChannelTableRow(ParameterType.IN, "", "", clazz));
			}
		}
		InputChannelTableRow row = _rowsForInputChannels.get(rowIndex);
		row.setDescription(description);
		row.setValueType(clazz);
	}
	
	public void setInputChannelValue(final int rowIndex, final Object value) {
		assert rowIndex < _rowsForInputChannels.size();
		InputChannelTableRow row = _rowsForInputChannels.get(rowIndex);
		row.setDefaultValue(value);
	}
	
	public List<InputChannelTableRow> getRowsWithContent(ParameterType type) {
		List<InputChannelTableRow> result = new ArrayList<InputChannelTableRow>();
		
		for(InputChannelTableRow row : getAllRows()) {
			String channel = row.getChannel();
			Object value = row.getDefaultValue();
			if (row.getParameterType() == type && 
					((channel!=null && channel.length()>0) || value!=null)) {
				result.add(row);
			}
		}
		
		return result;
	}
}
