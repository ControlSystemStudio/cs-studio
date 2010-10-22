package org.remotercp.preferences.ui;

public class EditableTableItem {
	private String key;
	private boolean isChanged;
	private String localValue;
	private String remoteValue;

	public String getKey() {
		return key;
	}

	public void setKey(String localKey) {
		this.key = localKey;
	}

	public String getLocalValue() {
		return localValue;
	}

	public void setLocalValue(String localValue) {
		this.localValue = localValue;
	}

	public String getRemoteValue() {
		return remoteValue;
	}

	public void setRemoteValue(String remoteValue) {
		this.remoteValue = remoteValue;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isEdited) {
		this.isChanged = isEdited;
	}
}