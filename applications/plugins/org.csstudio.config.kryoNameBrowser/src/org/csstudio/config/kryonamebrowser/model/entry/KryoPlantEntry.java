package org.csstudio.config.kryonamebrowser.model.entry;

public class KryoPlantEntry {

	private String name;
	private String label;
	private String explanation;

	private int id = -1;
	private int parent;

	private int numberOfPlants = -1;

	public KryoPlantEntry(String name, String label, String explanation,
			int id, int parent, int numberOfPlants) {
		this.name = name;
		this.label = label;
		this.explanation = explanation;
		this.id = id;
		this.parent = parent;
		this.numberOfPlants = numberOfPlants;
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
		this.numberOfPlants = kryoPlantEntry.numberOfPlants;
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

	public void setNumberOfPlants(int numberOfPlants) {
		this.numberOfPlants = numberOfPlants;
	}

}
