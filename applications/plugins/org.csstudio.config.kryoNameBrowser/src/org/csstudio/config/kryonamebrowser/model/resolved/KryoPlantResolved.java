package org.csstudio.config.kryonamebrowser.model.resolved;

import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;

/**
 * Represents a resolved Plant entry. The only difference from {@link KryoPlantEntry} is in the handling of
 * numberOfPlants.
 * <p>
 * In {@link KryoPlantEntry} the <i>numberOfPlants</i> can only be 1 (entry is allowed) and 0 (entry is not allowed).
 * <p>
 * <p>
 * In {@link KryoPlantResolved} the <i>numberOfPlants</i> can be 0...N meaning this is the actual number used or
 * negative number meaning the value is not used (it is not allowed by the corresponding plant).
 * </p>
 * 
 * @author Alen Vrecko
 */
public final class KryoPlantResolved extends KryoPlantEntry {

	public KryoPlantResolved(int id) {
		super(id);
	}

	public KryoPlantResolved(KryoPlantEntry kryoPlantEntry) {
		super(kryoPlantEntry);
	}

	public KryoPlantResolved(String name, String label, String explanation,
			int id, int parent, int numberOfPlants) {
		super(name, label, explanation, id, parent, numberOfPlants);
	}

	@Override
	public void setNumberOfPlants(int numberOfPlants) {
		this.numberOfPlants = numberOfPlants;
	}

}
