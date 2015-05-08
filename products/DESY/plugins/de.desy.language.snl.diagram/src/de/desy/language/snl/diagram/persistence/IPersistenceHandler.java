package de.desy.language.snl.diagram.persistence;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.diagram.model.SNLDiagram;

/**
 * Interface of all persistence handler used store and load the layout data of
 * SNL diagrams.
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
public interface IPersistenceHandler {

    /**
     * Loads the layout data for states and state-sets from the file specified
     * by the given <code>originalFilePath</code>.
     *
     * @param originalFilePath
     *            The path of the original *.st file
     * @return A map containing all layout data for states and state-sets
     */
    Map<String, StateLayoutData> loadStateLayoutData(IPath originalFilePath) throws Exception;

    /**
     * Loads the layout data for connections from the file specified by the
     * given <code>originalFilePath</code>.
     *
     * @param originalFilePath
     *            The path of the original *.st file
     * @return A map containing all layout data for connections
     */
    Map<String, List<Point>> loadConnectionLayoutData(IPath originalFilePath) throws Exception;

    /**
     * Stores the layout data of a complete diagram contained in the file
     * specified by <code>originalFilePath</code>.
     *
     * @param originalFilePath
     *            The path to the file containing the diagram
     * @param diagram
     *            The snl diagram to store
     */
    void store(IPath originalFilePath, SNLDiagram diagram) throws Exception;

}
