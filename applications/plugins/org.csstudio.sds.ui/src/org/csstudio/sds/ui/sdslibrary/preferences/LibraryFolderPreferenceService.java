package org.csstudio.sds.ui.sdslibrary.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class LibraryFolderPreferenceService {

	private static final String SERIALIZED_ITEM_REGEX = "([^,]+,(true|false),)+";

	private static final String TEMPLATE_LIBRARY_ITEMS = "template_library_items";
	private final IPreferenceStore preferenceStore;
	
	private final List<LibraryFolderPreferenceChangeListener> listeners;

	public LibraryFolderPreferenceService(IPreferenceStore preferenceStore) {
		assert preferenceStore != null : "Precondition failed: preferenceStore != null";

		this.preferenceStore = preferenceStore;
		this.listeners = new ArrayList<LibraryFolderPreferenceService.LibraryFolderPreferenceChangeListener>();
		
		preferenceStore.setDefault(TEMPLATE_LIBRARY_ITEMS, "");
		
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty().equals(TEMPLATE_LIBRARY_ITEMS)) {
					for (LibraryFolderPreferenceChangeListener listener : listeners) {
						listener.preferencesChanged();
					}
				}
			}
		});
	}

	public void saveLibraryFolderPreferenceItems(
			List<LibraryFolderPreferenceItem> items) {
		assert items != null : "Precondition failed: items != null";

		preferenceStore.setValue(TEMPLATE_LIBRARY_ITEMS, serializeItems(items));
	}

	public List<LibraryFolderPreferenceItem> loadLibraryItems() {
		List<LibraryFolderPreferenceItem> result;

		String serializedItems = preferenceStore
				.getString(TEMPLATE_LIBRARY_ITEMS);
		if (serializedItems.length() > 0) {
			result = deserializeItems(serializedItems);
		} else {
			result = new ArrayList<LibraryFolderPreferenceItem>();
		}

		assert result != null : "Postcondition failed: result != null";
		return result;
	}

	public void addChangeListener(LibraryFolderPreferenceChangeListener changeListener) {
		assert changeListener != null : "Precondition failed: changeListener != null";
		
		this.listeners.add(changeListener);
	}
	
	public void removeChangeListener(LibraryFolderPreferenceChangeListener changeListener) {
		assert changeListener != null : "Precondition failed: changeListener != null";
		
		this.listeners.remove(changeListener);
	}

	protected static String serializeItems(
			List<LibraryFolderPreferenceItem> items) {
		assert items != null : "Precondition failed: items != null";
		assert !items.isEmpty() : "Precondition failed: !items.isEmpty()";

		StringBuffer result = new StringBuffer("");
		for (LibraryFolderPreferenceItem libraryFolderPreferenceItem : items) {
			result.append(libraryFolderPreferenceItem.getFolderPath() + ","
					+ libraryFolderPreferenceItem.isChecked() + ",");
		}

		assert result != null : "Postcondition failed: result != null";
		return result.toString();
	}

	protected static List<LibraryFolderPreferenceItem> deserializeItems(
			String serializedItemsString) {
		assert serializedItemsString != null : "Precondition failed: serializedItemsString != null";
		assert isValidSerializedItemList(serializedItemsString) : "Precondition failed: isValidSerializedItemList(serializedItemsString)";

		String[] splitStrings = serializedItemsString.split(",");

		ArrayList<LibraryFolderPreferenceItem> result = new ArrayList<LibraryFolderPreferenceItem>();

		LibraryFolderPreferenceItem currentItem = null;
		for (int stringIndex = 0; stringIndex < splitStrings.length; stringIndex++) {
			if (stringIndex % 2 == 0) {
				// current element is folder path
				currentItem = new LibraryFolderPreferenceItem(
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

	public interface LibraryFolderPreferenceChangeListener {
		void preferencesChanged();
	}
}
