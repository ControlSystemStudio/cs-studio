package org.csstudio.config.kryonamebrowser.ui.filter;

import java.sql.SQLException;
import java.util.List;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoProcessEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;

public class Binder {

	public static final int BLANK_ENTRY_INDEX_OFFSET = -1;

	public static void bindObject(Combo first, KryoNameBrowserLogic logic) {
		try {
			List<KryoObjectEntry> choices = logic.findToplevelObjectChoices();
			first.setData(choices);

			String[] items = new String[choices.size() + 1];

			items[0] = "";
			int i = 1;

			for (KryoObjectEntry entry : choices) {
				items[i] = entry.getName();
				i++;
			}

			first.setItems(items);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public static void bindObject(final Combo first, final Combo second,
			final KryoNameBrowserLogic logic) {

		second.setEnabled(false);
		
		first.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = first.getSelectionIndex();
				if (index == 0) {
					second.select(0);
					second.setEnabled(false);
					second.notifyListeners(SWT.Selection, new Event());
				} else {

					List<KryoObjectEntry> data = (List<KryoObjectEntry>) first
							.getData();
					KryoObjectEntry entry = data.get(index + BLANK_ENTRY_INDEX_OFFSET);

					List<KryoObjectEntry> choices;
					try {
						choices = logic.findObjectChoices(entry);
					} catch (SQLException e1) {
						throw new RuntimeException(e1);
					}

					if (choices.size() == 0) {
						second.setEnabled(false);
						second.notifyListeners(SWT.Selection, new Event());
						return;

					}
					second.setData(choices);

					String[] items = new String[choices.size() + 1];

					items[0] = "";
					int i = 1;

					for (KryoObjectEntry entry1 : choices) {
						items[i] = entry1.getName();
						i++;
					}

					second.setItems(items);
					second.select(0);
					second.setEnabled(true);
					second.notifyListeners(SWT.Selection, new Event());

				}

			}
		});

	}
	
	public static void bindPlant(Combo first, KryoNameBrowserLogic logic) {
		try {
			List<KryoPlantEntry> choices = logic.findToplevelPlantChoices();
			first.setData(choices);

			String[] items = new String[choices.size() + 1];

			items[0] = "";
			int i = 1;

			for (KryoPlantEntry entry : choices) {
				items[i] = entry.getName();
				i++;
			}

			first.setItems(items);
		

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public static void bindPlant(final Combo first, final Combo second,
			final KryoNameBrowserLogic logic) {

		second.setEnabled(false);
		
		first.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = first.getSelectionIndex();
				if (index == 0) {
					second.select(0);
					second.setEnabled(false);
					second.notifyListeners(SWT.Selection, new Event());
				} else {

					List<KryoPlantEntry> data = (List<KryoPlantEntry>) first
							.getData();
					KryoPlantEntry entry = data.get(index + BLANK_ENTRY_INDEX_OFFSET);

					List<KryoPlantEntry> choices;
					try {
						choices = logic.findPlantChoices(entry);
					} catch (SQLException e1) {
						throw new RuntimeException(e1);
					}

					if (choices.size() == 0) {
						second.setEnabled(false);
						second.notifyListeners(SWT.Selection, new Event());
						return;

					}
					second.setData(choices);

					String[] items = new String[choices.size() + 1];

					items[0] = "";
					int i = 1;

					for (KryoPlantEntry entry1 : choices) {
						items[i] = entry1.getName();
						i++;
					}

					second.setItems(items);
					second.select(0);
					second.setEnabled(true);
					second.notifyListeners(SWT.Selection, new Event());

				}

			}
		});

	}
	
	public static void bindProcess(Combo first, KryoNameBrowserLogic logic) {
		try {
			List<KryoProcessEntry> choices = logic.findProcessChoices();
			first.setData(choices);

			String[] items = new String[choices.size() + 1];

			items[0] = "";
			int i = 1;

			for (KryoProcessEntry entry : choices) {
				items[i] = entry.getName();
				i++;
			}

			first.setItems(items);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

}
