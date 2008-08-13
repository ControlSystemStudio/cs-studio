package org.csstudio.config.kryonamebrowser.model.entry;

/**
 * Corresponds to the IO_NAME table entry in the database. Represents the actual name.
 *
 * @author Alen Vrecko
 */
public class KryoNameEntry {

    private int id = -1;
    private String name = "";
    private int plantId = -1;
    private int objectId = -1;
    private String processId;
    private int seqKryoNumber = -1;
    private String label;

    public KryoNameEntry(int id, String name, int plantId, int objectId, String processId, int seqKryoNumber, String label) {
        this.id = id;
        this.name = name;
        this.plantId = plantId;
        this.objectId = objectId;
        this.processId = processId;
        this.seqKryoNumber = seqKryoNumber;
        this.label = label;
    }

    public KryoNameEntry() {
        
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

    public String getProcessId() {
        return processId;
    }

    public int getSeqKryoNumber() {
        return seqKryoNumber;
    }

    public String getLabel() {
        return label;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setSeqKryoNumber(int seqKryoNumber) {
        this.seqKryoNumber = seqKryoNumber;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
