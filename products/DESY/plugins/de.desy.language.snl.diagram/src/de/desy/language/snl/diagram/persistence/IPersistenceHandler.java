package de.desy.language.snl.diagram.persistence;

import org.eclipse.core.runtime.IPath;

import de.desy.language.snl.diagram.model.SNLDiagram;

public interface IPersistenceHandler {

	void store(IPath originalFilePath, SNLDiagram diagram);

}
