package org.csstudio.config.kryonamebrowser.model.resolved;

import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoProcessEntry;

import java.util.LinkedList;
import java.util.List;

public class KryoNameResolved {

    private String name;

    private String label;

    private List<KryoPlantEntry> plants = new LinkedList<KryoPlantEntry>();

    private List<KryoObjectEntry> objects = new LinkedList<KryoObjectEntry>();

    private KryoProcessEntry process;

    private int id;

    private int seqKryoNumber;

    public KryoNameResolved(String name, String label, int id, int seqKryoNumber, KryoProcessEntry process) {
        this.name = name;
        this.label = label;
        this.id = id;
        this.seqKryoNumber = seqKryoNumber;
        this.process = process;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public List<KryoPlantEntry> getPlants() {
        return plants;
    }

    public List<KryoObjectEntry> getObjects() {
        return objects;
    }

    public KryoProcessEntry getProcess() {
        return process;
    }

    public int getId() {
        return id;
    }

    public int getSeqKryoNumber() {
        return seqKryoNumber;
    }
}
