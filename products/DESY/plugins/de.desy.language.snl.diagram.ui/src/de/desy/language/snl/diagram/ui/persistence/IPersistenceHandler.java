package de.desy.language.snl.diagram.ui.persistence;

import de.desy.language.snl.diagram.model.SNLDiagram;

public interface IPersistenceHandler {

	void store(String fileName, SNLDiagram diagram);

}
