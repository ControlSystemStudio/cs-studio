package org.csstudio.util.swt;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariableName;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

/**
 * Maintains a 'history' Combo box.
 * <p>
 * Newly entered items are added to the end of the combo list, dropping items
 * off the list when reaching a comfigurable maximum list size.
 * <p>
 * TODO Compare with the one in org.csstudio.apputil. This one has IPV items for
 * the context menu contrib, but that doesn't work that well anyway...
 * <p>
 * 
 * @see #newSelection(String)
 * @author Kay Kasemir
 * @author Helge Rickens
 */
public abstract class ComboHistoryHelper {
	
	private static final Logger log = Logger.getLogger(ComboHistoryHelper.class.getName());
	
	private static final String TAG = "values"; //$NON-NLS-1$
	private static final int DEFAULT_MAX = 10;
	private final IDialogSettings settings;
	private final String tag;
	private final ComboViewer combo;
	private final int max;

	/** Attach helper to given combo box, using default list length. */
	public ComboHistoryHelper(IDialogSettings settings, String tag,
			ComboViewer combo) {
		this(settings, tag, combo, DEFAULT_MAX);
	}

	/**
	 * Attach helper to given combo box, using max list length.
	 * 
	 * @param settings
	 *            Where to persist the combo box list
	 * @param tag
	 *            Tag used for persistence
	 * @param combo
	 *            The ComboViewer
	 * @param max
	 *            Max list length
	 */
	public ComboHistoryHelper(IDialogSettings settings, String tag,
			ComboViewer combo, int max) {
		this.settings = settings;
		this.tag = tag;
		this.combo = combo;
		this.max = max;
		// React whenever an existing entry is selected,
		// or a new name is entered.
		// New names are also added to the list.
		combo.getCombo().addSelectionListener(new SelectionListener() {
			// Called after <Return> was pressed
			public void widgetDefaultSelected(SelectionEvent e) {
				ProcessVariableName new_entry = new ProcessVariableName(
						ComboHistoryHelper.this.combo.getCombo().getText());
				addEntry(new_entry);
				newSelection(new_entry);
			}

			// Called after existing entry was picked from list
			public void widgetSelected(SelectionEvent e) {
				handleNewSelection();
			}
		});
		combo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ProcessVariableName) element).getProcessVariableName();
			}
		});

	}

	/** Add entry to the list. */
	@SuppressWarnings("nls")
	public void addEntry(ProcessVariableName pvName) {
		log.log(Level.FINE, "ComboHelper: Add {0}", pvName);

		// Locate & remove the entry to avoid duplicates.
		// A simple remove() would throw exception in case the elem isn't found.
		final Combo ctrl = combo.getCombo();
		boolean only_a_reorg = false;
		for (int i = 0; i < ctrl.getItemCount(); ++i) {
			final Object obj = combo.getElementAt(i);
			ProcessVariableName elem = (ProcessVariableName) obj;
			if (elem.equals(pvName)) {
				combo.remove(obj);
				only_a_reorg = true;
			}
		}
		// Maybe remove oldest (first) entry to keep list size <= max
		if (ctrl.getItemCount() >= max)
			combo.remove(combo.getElementAt(0));

		// Add new entry to the end
		combo.add(pvName);
		if (!only_a_reorg)
			ctrl.select(ctrl.getItemCount() - 1);

		if (log.isLoggable(Level.FINE))
			for (int i = 0; i < ctrl.getItemCount(); ++i)
				log.log(Level.FINE, "Item {0}: {1}", new Object[]{ i, combo.getElementAt(i)});
	}

	/** Notify about new selection. */
	private void handleNewSelection() {
		String name = combo.getCombo().getText();
		newSelection(new ProcessVariableName(name));
	}

	/** Invoked whenever a new entry was entered or selected. */
	public abstract void newSelection(ProcessVariableName entry);

	/** Load persisted list values. */
	public void loadSettings() {
		IDialogSettings pvs = settings.getSection(tag);
		if (pvs == null)
			return;
		String values[] = pvs.getArray(TAG);
		if (values != null)
			for (int i = 0; i < values.length; i++)
				combo.add(new ProcessVariableName(values[i]));
	}

	/** Save list values to persistent storage. */
	public void saveSettings() {
		IDialogSettings values = settings.addNewSection(tag);
		values.put(TAG, combo.getCombo().getItems());
	}
}
