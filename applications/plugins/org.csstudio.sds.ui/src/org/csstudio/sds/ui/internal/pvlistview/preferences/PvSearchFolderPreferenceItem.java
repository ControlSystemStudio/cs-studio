package org.csstudio.sds.ui.internal.pvlistview.preferences;


public class PvSearchFolderPreferenceItem {

	private boolean isChecked;
	private String folderPath;

	public PvSearchFolderPreferenceItem(String folderPath) {
		assert folderPath != null : "Precondition failed: folderPath != null";
		assert !folderPath.contains(",") : "Precondition failed: !folderPath.contains(\",\")";
		
		this.folderPath = folderPath;
		this.isChecked = true;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public String getFolderPath() {

		assert folderPath != null : "Postcondition failed: result != null";
		return folderPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((folderPath == null) ? 0 : folderPath.hashCode());
		result = prime * result + (isChecked ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PvSearchFolderPreferenceItem other = (PvSearchFolderPreferenceItem) obj;
		if (folderPath == null) {
			if (other.folderPath != null)
				return false;
		} else if (!folderPath.equals(other.folderPath))
			return false;
		if (isChecked != other.isChecked)
			return false;
		return true;
	}
	
	
}
