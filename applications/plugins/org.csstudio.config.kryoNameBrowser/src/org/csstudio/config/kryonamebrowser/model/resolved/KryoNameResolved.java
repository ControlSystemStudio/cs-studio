package org.csstudio.config.kryonamebrowser.model.resolved;

import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoProcessEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KryoNameResolved {

    private String name;

    private String label;

    private List<KryoPlantResolved> plants = new ArrayList<KryoPlantResolved>();

    private List<KryoObjectEntry> objects = new ArrayList<KryoObjectEntry>();

    private KryoProcessEntry process;

    private int id = -1;

    private int seqKryoNumber = -1;

    public KryoNameResolved(String name, String label, int id, int seqKryoNumber, KryoProcessEntry process) {
        this.name = name;
        this.label = label;
        this.id = id;
        this.seqKryoNumber = seqKryoNumber;
        this.process = process;
    }

    public KryoNameResolved() {
	
	}

	public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public List<KryoPlantResolved> getPlants() {
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

	public void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPlants(List<KryoPlantResolved> plants) {
		this.plants = plants;
	}

	public void setObjects(List<KryoObjectEntry> objects) {
		this.objects = objects;
	}

	public void setProcess(KryoProcessEntry process) {
		this.process = process;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSeqKryoNumber(int seqKryoNumber) {
		this.seqKryoNumber = seqKryoNumber;
	}
    
    
}
