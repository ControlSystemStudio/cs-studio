package org.csstudio.config.kryonamebrowser.model.resolved;

import java.util.List;

import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoProcessEntry;

public class KryoNameResolved {

	private String name;

	private List<KryoPlantEntry> plants;

	private List<KryoObjectEntry> objects;

	private KryoProcessEntry process;

}
