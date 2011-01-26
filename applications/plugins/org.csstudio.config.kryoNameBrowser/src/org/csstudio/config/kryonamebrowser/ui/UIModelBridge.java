package org.csstudio.config.kryonamebrowser.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoPlantResolved;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * Responsible for syncing the complex combo with the model.
 * 
 * @author Alen Vrecko
 */
public class UIModelBridge {

	private static final int EMPTY_ENTRY_OFFSET = -1;
	private List<SelectionListener> listeners = new ArrayList<SelectionListener>();

	private Combo plant;
	private Text plantNo;
	private Combo subplant1;
	private Text subplant1No;
	private Combo subplant2;
	private Text subplant2No;
	private Combo subplant3;
	private Text subplant3No;
	private Combo object;
	private Combo function;
	private Combo subfunction;
//	private Combo process;
	private Text number;
	private Text description;

	private boolean bound = false;
	private KryoNameBrowserLogic logic;

	public void registerPlant(Combo combo, Text no) {
		this.plant = combo;
		this.plantNo = no;
	}

	public void registerSubPlant1(Combo combo, Text no) {
		this.subplant1 = combo;
		this.subplant1No = no;
	}

	public void registerSubPlant2(Combo combo, Text no) {
		this.subplant2 = combo;
		this.subplant2No = no;
	}

	public void registerSubPlant3(Combo combo, Text no) {
		this.subplant3 = combo;
		this.subplant3No = no;
	}

	public void registerObject(Combo combo) {
		this.object = combo;
	}

	public void registerFunction(Combo combo) {
		this.function = combo;
	}

	public void registerSubfunction(Combo combo) {
		this.subfunction = combo;
	}

//	public void registerProcess(Combo combo) {
//		this.process = combo;
//
//	}

	public void registerKryoNumber(Text text) {
		this.number = text;

	}

	public void registerDescription(Text text) {
		this.description = text;

	}

	public void setLogic(KryoNameBrowserLogic logic) {
		this.logic = logic;

	}

	/**
	 * Realizes all the bindings. Should call this first before calling any get methods.
	 */
	public void bind() {
		if (bound) {
			throw new IllegalStateException("Should only be called once!");
		}
		bound = true;

		populatePlant(plant, new KryoPlantEntry(
				KryoNameBrowserLogic.NO_PARENT_PLANT_ID));

		plant.addSelectionListener(new PlantSelectionListener(plant, plantNo,
				new Combo[] { subplant1, subplant2, subplant3 }, new Text[] {
						subplant1No, subplant2No, subplant3No }));

		subplant1.addSelectionListener(new PlantSelectionListener(subplant1,
				subplant1No, new Combo[] { subplant2, subplant3 }, new Text[] {
						subplant2No, subplant3No }));

		subplant2.addSelectionListener(new PlantSelectionListener(subplant2,
				subplant2No, new Combo[] { subplant3 },
				new Text[] { subplant3No }));

		subplant3.addSelectionListener(new PlantSelectionListener(subplant3,
				subplant3No, new Combo[] {}, new Text[] {}));

		plant.setEnabled(true);

		populateObject(object, new KryoObjectEntry(
				KryoNameBrowserLogic.NO_PARENT_OBJECT_ID));

		object.addSelectionListener(new ObjectSelectionListener(object,
				new Combo[] { function, subfunction }));

		function.addSelectionListener(new ObjectSelectionListener(function,
				new Combo[] { subfunction }));
		subfunction.addSelectionListener(new ObjectSelectionListener(
				subfunction, new Combo[] {}));
		object.setEnabled(true);

//		populateProcess(process);
//		process.addSelectionListener(new SelectionListener() {
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				fireListeners();
//
//			}
//		});

		Text[] text = new Text[] { plantNo, subplant1No, subplant2No,
				subplant3No, number };

		for (Text text2 : text) {
			if (text2 != null) {
				text2.addModifyListener(new ModifyListener() {

					public void modifyText(ModifyEvent e) {
						fireListeners();

					}
				});
			}
		}

	}

//	precessid is now combined with seq_number
//	private void populateProcess(Combo plant2) {
//		try {
//			List<KryoProcessEntry> choices = logic.findProcessChoices();
//			plant2.setData(choices);
//
//			if (choices.size() == 0) {
//				plant2.setText("");
//				plant2.setEnabled(false);
//				return;
//			}
//
//			String[] items = new String[choices.size() + 1];
//
//			items[0] = "";
//			int i = 1;
//
//			for (KryoProcessEntry entry : choices) {
//				items[i] = entry.getName() + " (" + entry.getId() + ")";
//				i++;
//			}
//
//			plant2.setItems(items);
//
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
//
//	}

	private void populatePlant(Combo plant2, KryoPlantEntry parent) {
		try {
			List<KryoPlantEntry> choices = logic.findPlantChoices(parent);
			plant2.setData(choices);

			if (choices.size() == 0) {
				plant2.setText("");
				plant2.setEnabled(false);
				return;
			}

			String[] items = new String[choices.size() + 1];

			items[0] = "";
			int i = 1;

			for (KryoPlantEntry entry : choices) {
				items[i] = entry.getName() + " (" + entry.getLabel() + ")";
				i++;
			}

			plant2.setItems(items);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private Object getEntry(Combo combo) {
		if (combo == null || !combo.isEnabled()) {
			return null;
		}

		List entries = (List) combo.getData();

		if (entries == null) {
			return null;
		}

		int selected = combo.getSelectionIndex() + EMPTY_ENTRY_OFFSET;

		if (selected >= entries.size() || selected < 0) {
			return null;
		}

		return entries.get(selected);

	}

	private class PlantSelectionListener implements SelectionListener {

		private final Combo primary;
		private final Text no;
		private final Combo[] dependantCombo;
		private final Text[] dependantText;

		public PlantSelectionListener(Combo primary, Text no,
				Combo[] dependantCombo, Text[] dependantText) {
			this.primary = primary;
			this.no = no;
			this.dependantCombo = dependantCombo;
			this.dependantText = dependantText;

			primary.setEnabled(false);
			no.setEnabled(false);

		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			if (!primary.isEnabled()) {
				return;
			}

			for (Combo combo : dependantCombo) {
				combo.setText("");
				combo.setEnabled(false);
			}

			for (Text text : dependantText) {
				text.setText("");
				text.setEnabled(false);
			}

			if (primary.getSelectionIndex() > 0) {

				// handle number of plants
				KryoPlantEntry selection = (KryoPlantEntry) getEntry(primary);

				if (selection.getNumberOfPlants() > 0) {
					no.setText("");
					no.setEnabled(true);
				} else {
					no.setText("");
					no.setEnabled(false);
				}

				// get next in line and populate it

				if (dependantCombo.length == 0) {
					return;
				}

				Combo dependant = dependantCombo[0];
				dependant.setEnabled(true);

				populatePlant(dependant, selection);

			} else {
				no.setText("");
				no.setEnabled(false);
			}

			fireListeners();

		}

	}

	private void populateObject(Combo plant2, KryoObjectEntry parent) {
		try {
			List<KryoObjectEntry> choices = logic.findObjectChoices(parent);
			plant2.setData(choices);

			if (choices.size() == 0) {
				plant2.setText("");
				plant2.setEnabled(false);
				return;
			}

			String[] items = new String[choices.size() + 1];

			items[0] = "";
			int i = 1;

			for (KryoObjectEntry entry : choices) {
				items[i] = entry.getName() + " (" + entry.getLabel() + ")";
				i++;
			}

			plant2.setItems(items);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	private class ObjectSelectionListener implements SelectionListener {

		private final Combo primary;

		private final Combo[] dependantCombo;

		public ObjectSelectionListener(Combo primary, Combo[] dependantCombo) {
			this.primary = primary;

			this.dependantCombo = dependantCombo;

			primary.setEnabled(false);

		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			if (!primary.isEnabled()) {
				return;
			}

			for (Combo combo : dependantCombo) {
				combo.setText("");
				combo.setEnabled(false);
			}

			if (primary.getSelectionIndex() > 0) {

				// get next in line and populate it

				if (dependantCombo.length == 0) {
					return;
				}

				Combo dependant = dependantCombo[0];
				dependant.setEnabled(true);

				populateObject(dependant, (KryoObjectEntry) getEntry(primary));

			}

			fireListeners();

		}

	}

	private void handlePlants(KryoNameResolved resolved, Combo[] plants,
			Text[] no) {

		for (int i = 0; i < plants.length; i++) {
			KryoPlantEntry entry = (KryoPlantEntry) getEntry(plants[i]);

			if (entry != null) {
				KryoPlantResolved plantResolved = new KryoPlantResolved(entry);
				plantResolved.setNumberOfPlants(getNo(no[i]));
				resolved.getPlants().add(plantResolved);
			}

		}

	}

	private void handleObjects(KryoNameResolved resolved, Combo[] objects) {

		for (int i = 0; i < objects.length; i++) {
			KryoObjectEntry entry = (KryoObjectEntry) getEntry(objects[i]);

			if (entry != null) {
				resolved.getObjects().add(entry);
			}

		}

	}

	/**
	 * Returns a {@link KryoNameResolved} that is used by filtering/search. Description and name are ignored.
	 * 
	 * @return
	 */
	public KryoNameResolved calculateExampleEntry() {
		KryoNameResolved example = new KryoNameResolved();

		// handle plant
		handlePlants(example, new Combo[] { plant, subplant1, subplant2,
				subplant3 }, new Text[] { plantNo, subplant1No, subplant2No,
				subplant3No });

		// handle objects
		handleObjects(example, new Combo[] { object, function, subfunction });

		// get process if applicable

//      precess id is now comined with seq number
//		example.setProcess((KryoProcessEntry) getEntry(process));

		example.setSeqKryoNumber(getNo(number));

		return example;
	}

	/**
	 * Uses the data from UI to create a new name to be written in the database. You should call {@link #validate()}
	 * before using this method.
	 * 
	 * @return
	 */
	public KryoNameEntry calculateNewEntrty() {

		KryoNameResolved example = calculateExampleEntry();

		KryoNameEntry newEntry = new KryoNameEntry();
		newEntry.setName(calculateName());

		if (example.getPlants().size() > 0) {
			newEntry.setPlantId(example.getPlants().get(
					example.getPlants().size() - 1).getId());
		}

		if (example.getObjects().size() > 0) {
			newEntry.setObjectId(example.getObjects().get(
					example.getObjects().size() - 1).getId());
		}

//		KryoProcessEntry process = example.getProcess();

//		newEntry.setProcessId(process != null ? process.getId() : null);

		newEntry.setSeqKryoNumber(example.getSeqKryoNumber());

		newEntry.setLabel(description.getText());

		return newEntry;

	}

	/**
	 * Calculates the name of name entry for the given UI selection.
	 * 
	 * @return
	 */
	public String calculateName() {

		KryoNameResolved example = calculateExampleEntry();

		StringBuilder builder = new StringBuilder();

		// TODO: Hardcoded value
		builder.append("X");

		List<KryoPlantResolved> plants = example.getPlants();

		for (KryoPlantEntry kryoPlantEntry : plants) {
			builder.append(kryoPlantEntry.getLabel());
			if (kryoPlantEntry.getNumberOfPlants() >= 0) {
				builder.append(kryoPlantEntry.getNumberOfPlants());
			}
		}

		builder.append(":");

		for (KryoObjectEntry kOEntry : example.getObjects()) {
			builder.append(kOEntry.getLabel());
		}

//		KryoProcessEntry process = example.getProcess();
//		builder.append(process != null ? process.getId() : "");
		StringBuffer seqKryoNumber = new StringBuffer(String.valueOf(example.getSeqKryoNumber()));
		while (seqKryoNumber.length() < 4) {
		    seqKryoNumber.insert(0, "0");
		}
		builder.append(seqKryoNumber);

		String string = builder.toString();
		return string;

	}

	/**
	 * Checks weather all the fields are properly filled in order for adding a new entry.
	 * 
	 * @return
	 */
	public boolean validate() {
		// validate plants and objects and process

		// only top level plant has to be selected
		if (plant.isEnabled() && getEntry(plant) == null) {
			return false;
		}

		// objects must all be provided
		Combo[] objects = new Combo[] { object, function, subfunction };

		for (Combo combo : objects) {
			if (combo.isEnabled() && getEntry(combo) == null) {
				return false;
			}
		}

		Text[] text = new Text[] { plantNo, subplant1No, subplant2No,
				subplant3No };

		if (number.isEnabled() && getNo(number) < 0) {
				return false;
		}

		int i = 0;
		Combo[] plants = new Combo[] { plant, subplant1, subplant2, subplant3 };
		for (Combo combo : plants) {

			KryoPlantEntry entry = (KryoPlantEntry) getEntry(combo);
			if (combo.isEnabled() && entry != null
					&& entry.getNumberOfPlants() > 0 && getNo(text[i]) < 0) {
				if (text[i].getText().length() != 0) {
					return false;
				}
			}
			i++;
		}

		return true;

	}

	// TODO: Add column

	private int getNo(Text text) {
		if (text == null || !text.isEnabled() || (text.getText().length() == 0)) {
			return -1;
		}

		try {
			return Integer.parseInt(text.getText());
		} catch (NumberFormatException e) {
			return -1;
		}

	}

	public void addListener(SelectionListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	private void fireListeners() {
		for (SelectionListener listener : listeners) {
			listener.widgetSelected(null);
		}
	}

	/**
	 * Loads the UI with the values from the parameter.
	 * 
	 * @param resolved
	 */
	public void load(KryoNameResolved resolved, boolean shouldEnable) {

		List<KryoPlantResolved> plants = resolved.getPlants();

		Combo[] plantArray = new Combo[] { plant, subplant1, subplant2,
				subplant3 };

		Text[] textArray = new Text[] { plantNo, subplant1No, subplant2No,
				subplant3No };

		for (int i = 0; i < plants.size(); i++) {
			Combo combo = plantArray[i];
			combo.setEnabled(shouldEnable);
			KryoPlantResolved plantResolved = plants.get(i);
			populatePlant(combo, new KryoPlantEntry(plantResolved.getParent()));

			String[] items = combo.getItems();
			for (int j = 0; j < items.length; j++) {
				String string = items[j];
				if (string.equals(plantResolved.getName() + " ("
						+ plantResolved.getLabel() + ")")) {
					combo.select(j);
				}
			}

			int ofPlants = plantResolved.getNumberOfPlants();
			if (ofPlants >= 0) {
				textArray[i].setText("" + ofPlants);
			}

			if (shouldEnable && ofPlants >= 0) {
				textArray[i].setEnabled(true);
			} else {
				textArray[i].setEnabled(false);
			}

		}

		Combo[] objectArray = new Combo[] { object, function, subfunction };

		List<KryoObjectEntry> objects = resolved.getObjects();

		for (int i = 0; i < objects.size(); i++) {
			Combo combo = objectArray[i];
			combo.setEnabled(shouldEnable);
			KryoObjectEntry objectResolved = objects.get(i);
			populateObject(combo, new KryoObjectEntry(objectResolved
					.getParent()));

			String[] items = combo.getItems();
			for (int j = 0; j < items.length; j++) {
				String string = items[j];
				if (string.equals(objectResolved.getName() + " ("
						+ objectResolved.getLabel() + ")")) {
					combo.select(j);
				}
			}

		}

//		populateProcess(process);
//
//		String[] items = process.getItems();
//
//		for (int i = 0; i < items.length; i++) {
//			if (items[i].equals(resolved.getProcess().getName() + " ("
//					+ resolved.getProcess().getId() + ")")) {
//				process.select(i);
//			}
//		}

		number.setText("" + resolved.getSeqKryoNumber());

		description.setText(resolved.getLabel());

		if (shouldEnable) {
//			process.setEnabled(true);
			number.setEnabled(true);
		} else {
//			process.setEnabled(false);
			number.setEnabled(false);
		}

	}

}
