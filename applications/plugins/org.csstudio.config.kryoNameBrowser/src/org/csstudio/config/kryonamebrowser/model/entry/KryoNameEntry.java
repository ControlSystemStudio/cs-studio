package org.csstudio.config.kryonamebrowser.model.entry;

/**
 * Corresponds to the IO_NAME table entry in the database. Represents the actual name.
 *
 * @author Alen Vrecko
 */
public class KryoNameEntry {

    private int id;
    private String name;
    private int plantId;
    private int objectId;
    private int processId;
    private int seqKryoNumber;
    private String label;

    public KryoNameEntry(int id, String name, int plantId, int objectId, int processId, int seqKryoNumber, String label) {
        this.id = id;
        this.name = name;
        this.plantId = plantId;
        this.objectId = objectId;
        this.processId = processId;
        this.seqKryoNumber = seqKryoNumber;
        this.label = label;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPlantId() {
        return plantId;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getProcessId() {
        return processId;
    }

    public int getSeqKryoNumber() {
        return seqKryoNumber;
    }

    public String getLabel() {
        return label;
    }
}
