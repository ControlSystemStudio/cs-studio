package org.csstudio.config.kryonamebrowser.model.entry;

import org.csstudio.config.kryonamebrowser.database.TableNames;

/**
 * Represents entry (row) in the {@link TableNames#PLANTS_TABLE} table of the database.
 * 
 * @author Alen Vrecko
 * 
 */
public class KryoPlantEntry {

	private String name;
	private String label;
	private String explanation;

	private int id = -1;
	private int parent;

	protected int numberOfPlants = -1;

	public KryoPlantEntry(String name, String label, String explanation,
			int id, int parent, int numberOfPlants) {
		this.name = name;
		this.label = label;
		this.explanation = explanation;
		this.id = id;
		this.parent = parent;
		setNumberOfPlants(numberOfPlants);
	}

	public KryoPlantEntry(int id) {
		this.id = id;
	}

	public KryoPlantEntry(KryoPlantEntry kryoPlantEntry) {
		this.name = kryoPlantEntry.name;
		this.label = kryoPlantEntry.label;
		this.explanation = kryoPlantEntry.explanation;
		this.id = kryoPlantEntry.id;
		this.parent = kryoPlantEntry.parent;
		setNumberOfPlants(kryoPlantEntry.numberOfPlants);
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public String getExplanation() {
		return explanation;
	}

	public int getId() {
		return id;
	}

	public int getParent() {
		return parent;
	}

	public int getNumberOfPlants() {
		return numberOfPlants;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public void setId(int id) {
		this.id = id;

	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	/**
	 * Sets the number of plants. Can only be 1 - entry allowed or 0 - entry not allowed.
	 * 
	 * @param numberOfPlants
	 */
	public void setNumberOfPlants(int numberOfPlants) {
		if (numberOfPlants == 0 || numberOfPlants == 1) {
			this.numberOfPlants = numberOfPlants;
		} else {
			throw new IllegalArgumentException("Must provide 1 or 0");
		}
	}

}
