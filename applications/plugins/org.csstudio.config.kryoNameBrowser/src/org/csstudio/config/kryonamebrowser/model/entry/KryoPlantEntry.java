package org.csstudio.config.kryonamebrowser.model.entry;

public class KryoPlantEntry {

    private String name;
    private String label;
    private String explanation;

    private int id = -1;
    private int parent;

    private int numberOfPlants;


    public KryoPlantEntry(String name, String label, String explanation, int id, int parent, int numberOfPlants) {
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
}
