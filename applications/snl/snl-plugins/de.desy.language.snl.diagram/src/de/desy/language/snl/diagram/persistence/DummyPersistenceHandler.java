package de.desy.language.snl.diagram.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.diagram.model.SNLDiagram;
import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.WhenConnection;

public class DummyPersistenceHandler implements IPersistenceHandler {

    @Override
    public void store(IPath originalFilePath, SNLDiagram diagram) {
        StringBuffer output = new StringBuffer("<file>\"");
        output.append(originalFilePath.lastSegment());
        output.append("\"\n");
        List<SNLModel> children = diagram.getChildren();
        output.append(generateOutput("\t", children));
        output.append("</file>\n");
        writeOutput(output.toString());
    }

    private String generateOutput(String prefix, List<SNLModel> children) {
        StringBuffer result = new StringBuffer();
        String subPrefix = prefix + "\t";
        for (SNLModel current : children) {
            String identifier = current.getIdentifier();
            result.append(prefix);
            result.append("<identifier>\"");
            result.append(identifier);
            result.append("\"\n");
            result.append(addLocation(subPrefix, current.getLocation()));
            List<WhenConnection> connections = current.getSourceConnections();
            for (WhenConnection connection : connections) {
                result.append(addConnection(subPrefix, connection));
            }
            result.append(prefix);
            result.append("</identifier>\n");
        }
        return result.toString();
    }

    private String addConnection(String prefix, WhenConnection connection) {
        StringBuffer result = new StringBuffer(prefix);
        String subPrefix = prefix + "\t";
        result.append("<connection>\"");
        result.append(connection.getIdentifier());
        result.append("\"\n");
        for (Point bendPoint : connection.getBendPoints()) {
            result.append(addLocation(subPrefix, bendPoint));
        }
        result.append(prefix);
        result.append("</connection>\n");
        return result.toString();
    }

    private String addLocation(String prefix, Point location) {
        StringBuffer result = new StringBuffer(prefix);
        result.append("<Point>\"");
        result.append(location.x);
        result.append(",");
        result.append(location.y);
        result.append("\"</Point>\n");
        return result.toString();
    }

    private void writeOutput(String output) {
        System.out.println(output);
    }

    @Override
    public Map<String, List<Point>> loadConnectionLayoutData(
            IPath originalFilePath) {
        return new HashMap<String, List<Point>>();
    }

    @Override
    public Map<String, StateLayoutData> loadStateLayoutData(
            IPath originalFilePath) {
        return new HashMap<String, StateLayoutData>();
    }

}
