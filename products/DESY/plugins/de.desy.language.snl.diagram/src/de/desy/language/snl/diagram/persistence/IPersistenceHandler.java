package de.desy.language.snl.diagram.persistence;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.diagram.model.SNLDiagram;

public interface IPersistenceHandler {
	
	Map<String, StateLayoutData> loadStateLayoutData(IPath originalFilePath);
	
	Map<String, List<Point>> loadConnectionLayoutData(IPath originalFilePath);

	void store(IPath originalFilePath, SNLDiagram diagram);

}
