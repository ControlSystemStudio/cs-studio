package org.csstudio.sds.ui.internal.pvlistview.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class PvSearchFolderPreferenceService {

	private static final String SERIALIZED_ITEM_REGEX = "([^,]+,(true|false),)+";

	private static final String PV_SEARCH_FOLDER_ITEMS = "pv_search_folder_items";
	private final IPreferenceStore preferenceStore;
	
	private final List<PvSearchFolderPreferenceChangeListener> listeners;

	public PvSearchFolderPreferenceService(IPreferenceStore preferenceStore) {
		assert preferenceStore != null : "Precondition failed: preferenceStore != null";

		this.preferenceStore = preferenceStore;
		this.listeners = new ArrayList<PvSearchFolderPreferenceService.PvSearchFolderPreferenceChangeListener>();
		
		preferenceStore.setDefault(PV_SEARCH_FOLDER_ITEMS, "");
		
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty().equals(PV_SEARCH_FOLDER_ITEMS)) {
					for (PvSearchFolderPreferenceChangeListener listener : listeners) {
						listener.preferencesChanged();
					}
				}
			}
		});
	}

	public void saveLibraryFolderPreferenceItems(
			List<PvSearchFolderPreferenceItem> items) {
		assert items != null : "Precondition failed: items != null";

		preferenceStore.setValue(PV_SEARCH_FOLDER_ITEMS, serializeItems(items));
	}

	public List<PvSearchFolderPreferenceItem> loadPvSearchItems() {
		List<PvSearchFolderPreferenceItem> result;

		String serializedItems = preferenceStore
				.getString(PV_SEARCH_FOLDER_ITEMS);
		if (serializedItems.length() > 0) {
			result = deserializeItems(serializedItems);
		} else {
			result = new ArrayList<PvSearchFolderPreferenceItem>();
		}

		assert result != null : "Postcondition failed: result != null";
		return result;
	}

	public void addChangeListener(PvSearchFolderPreferenceChangeListener changeListener) {
		assert changeListener != null : "Precondition failed: changeListener != null";
		
		this.listeners.add(changeListener);
	}
	
	public void removeChangeListener(PvSearchFolderPreferenceChangeListener changeListener) {
		assert changeListener != null : "Precondition failed: changeListener != null";
		
		this.listeners.remove(changeListener);
	}

	protected static String serializeItems(
			List<PvSearchFolderPreferenceItem> items) {
		assert items != null : "Precondition failed: items != null";
		assert !items.isEmpty() : "Precondition failed: !items.isEmpty()";

		StringBuffer result = new StringBuffer("");
		for (PvSearchFolderPreferenceItem libraryFolderPreferenceItem : items) {
			result.append(libraryFolderPreferenceItem.getFolderPath() + ","
					+ libraryFolderPreferenceItem.isChecked() + ",");
		}

		assert result != null : "Postcondition failed: result != null";
		return result.toString();
	}

	protected static List<PvSearchFolderPreferenceItem> deserializeItems(
			String serializedItemsString) {
		assert serializedItemsString != null : "Precondition failed: serializedItemsString != null";
		assert isValidSerializedItemList(serializedItemsString) : "Precondition failed: isValidSerializedItemList(serializedItemsString)";

		String[] splitStrings = serializedItemsString.split(",");

		ArrayList<PvSearchFolderPreferenceItem> result = new ArrayList<PvSearchFolderPreferenceItem>();

		PvSearchFolderPreferenceItem currentItem = null;
		for (int stringIndex = 0; stringIndex < splitStrings.length; stringIndex++) {
			if (stringIndex % 2 == 0) {
				// current element is folder path
				currentItem = new PvSearchFolderPreferenceItem(
						splitStrings[stringIndex]);
			} else {
				// current element is checked state of folder
				currentItem.setChecked(Boolean
						.parseBoolean(splitStrings[stringIndex]));
				result.add(currentItem);
			}
		}
		return result;
	}

	protected static boolean isValidSerializedItemList(
			String serializedItemsString) {
		assert serializedItemsString != null : "Precondition failed: serializedItemsString != null";

		return serializedItemsString.matches(SERIALIZED_ITEM_REGEX);
	}

	public interface PvSearchFolderPreferenceChangeListener {
		void preferencesChanged();
	}
}
