package org.csstudio.config.kryonamebrowser.model.entry;

import org.csstudio.config.kryonamebrowser.database.TableNames;


/**
 * Corresponds to the {@link TableNames#OBJECTS_TABLE} table entry in the database. Represents the actual name.
 *
 * @author Alen Vrecko
 */
public class KryoObjectEntry {

    private String name;
    private String explanation;
    private int id;
    private int parent;
    private String label;
    private int level;

    public KryoObjectEntry(String name, String explanation, int id, int parent, String label, int level) {
        this.name = name;
        this.explanation = explanation;
        this.id = id;
        this.parent = parent;
        this.label = label;
        this.level = level;
    }

    public KryoObjectEntry(int id) {
        this.id = id;
    }

    public KryoObjectEntry(KryoObjectEntry kryoObjectEntry) {
    	  this.name = kryoObjectEntry.name;
          this.explanation = kryoObjectEntry.explanation;
          this.id = kryoObjectEntry.id;
          this.parent = kryoObjectEntry.parent;
          this.label = kryoObjectEntry.label;
          this.level = kryoObjectEntry.level;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
