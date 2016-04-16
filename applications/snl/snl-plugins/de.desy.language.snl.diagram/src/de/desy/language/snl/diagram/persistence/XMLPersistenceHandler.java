package de.desy.language.snl.diagram.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Point;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.helpers.DefaultHandler;

import de.desy.language.snl.diagram.model.SNLDiagram;
import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.StateModel;
import de.desy.language.snl.diagram.model.StateSetModel;
import de.desy.language.snl.diagram.model.WhenConnection;

/**
 * Stores and loads the diagram layout to a XML file.
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
public class XMLPersistenceHandler implements IPersistenceHandler {

    /**
     * {@inheritDoc}
     *
     * @throws Exception
     *             if data could not be stored
     * @require originalFilePath != null
     * @require diagram != null
     */
    @Override
    public void store(IPath originalFilePath, SNLDiagram diagram)
            throws Exception {
        assert originalFilePath != null : "originalFilePath != null";
        assert diagram != null : "diagram != null";

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Element diagramElement = createDiagramTag(diagram);

        IPath layoutDataPath = getLayoutDataPath(originalFilePath);

        try {
            FileOutputStream outputStream = new FileOutputStream(layoutDataPath
                    .toFile());
            outputter.output(diagramElement, outputStream);
        } catch (Exception e) {
            throw new Exception("Exception occurred while storing layout data",
                    e);
        }
    }

    /**
     * Determines the path for file containing the layout data.
     *
     * @param originalFilePath
     *            The path to the *.st file
     * @return The path
     * @require originalFilePath != null
     * @ensure result != null
     */
    private IPath getLayoutDataPath(IPath originalFilePath) {
        assert originalFilePath != null : "originalFilePath != null";

        String fileExtension = originalFilePath.getFileExtension();
        String fileName = originalFilePath.lastSegment();
        fileName = fileName.replace("." + fileExtension, ".layout");

        int segmentCount = originalFilePath.segmentCount();
        IPath constraintFilePath = originalFilePath
                .uptoSegment(segmentCount - 1);
        constraintFilePath = constraintFilePath.append(fileName);

        IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot()
                .getLocation();
        IPath result = workspacePath.append(constraintFilePath);

        assert result != null : "result != null";
        return result;
    }

    /**
     * Creates a XML element for the diagram
     *
     * @param diagram
     *            the diagram to store
     * @return The corresponding {@link Element}
     * @require diagram != null
     * @ensure rootElement != null
     */
    private Element createDiagramTag(SNLDiagram diagram) {
        assert diagram != null : "diagram != null";
        Element rootElement = new Element(XMLConstant.DIAGRAM.getIdentifier());
        rootElement.setAttribute(XMLConstant.NAME.getIdentifier(), diagram
                .getIdentifier());

        rootElement.addContent(createStateDataTag(diagram));
        rootElement.addContent(createConnectionDataTag(diagram));

        assert rootElement != null : "rootElement != null";
        return rootElement;
    }

    /**
     * Creates a XML Element for all connections.
     *
     * @param diagram
     *            The diagram to store
     * @require diagram != null
     * @ensure connectionDataElement != null
     */
    private Element createConnectionDataTag(SNLDiagram diagram) {
        assert diagram != null : "diagram != null";

        Element connectionDataElement = new Element(XMLConstant.CONNECTION_DATA
                .getIdentifier());
        for (SNLModel model : diagram.getChildren()) {
            List<WhenConnection> sourceConnections = model
                    .getSourceConnections();
            if (!sourceConnections.isEmpty()) {
                for (WhenConnection connection : sourceConnections) {
                    connectionDataElement.addContent(createConnectionTag(
                            connection, model.getIdentifier()));
                }
            }
        }

        assert connectionDataElement != null : "connectionDataElement != null";
        return connectionDataElement;
    }

    /**
     * Creates a XML element for a connection
     *
     * @param connection
     *            The connection to store
     * @param sourceIdentifier
     *            The identifier of the source state
     * @return The corresponding element
     * @require connection != null
     * @require sourceIdentifier != null
     * @require sourceIdentifier.trim().length() > 0
     * @ensure connectionElement != null
     */
    private Element createConnectionTag(WhenConnection connection,
            String sourceIdentifier) {
        assert connection != null : "connection != null";
        assert sourceIdentifier != null : "sourceIdentifier != null";
        assert sourceIdentifier.trim().length() > 0 : "${param}.trim().length() > 0";

        Element connectionElement = new Element(XMLConstant.CONNECTION
                .getIdentifier());
        connectionElement.setAttribute(XMLConstant.NAME.getIdentifier(),
                connection.getPropertyValue(SNLModel.PARENT) + "."
                        + sourceIdentifier + ".(" + connection.getIdentifier()
                        + ")");
        for (Point current : connection.getBendPoints()) {
            connectionElement.addContent(createPointTag(current));
        }
        assert connectionElement != null : "connectionElement != null";
        return connectionElement;
    }

    /**
     * Creates a XML element for a point
     *
     * @param point
     *            The point to store
     * @return The corresponding element
     * @require point != null
     * @ensure pointElement != null
     */
    private Element createPointTag(Point point) {
        assert point != null : "point != null";

        Element pointElement = new Element(XMLConstant.POINT.getIdentifier());
        pointElement.setAttribute(XMLConstant.LOCATION_X.getIdentifier(),
                String.valueOf(point.x));
        pointElement.setAttribute(XMLConstant.LOCATION_Y.getIdentifier(),
                String.valueOf(point.y));

        assert pointElement != null : "pointElement != null";
        return pointElement;
    }

    /**
     * Create a XML element for all states and state-sets.
     *
     * @param diagram
     *            The diagram to store
     * @return The corresponding element
     * @require diagram != null
     * @ensure stateDataElement != null
     */
    private Element createStateDataTag(SNLDiagram diagram) {
        assert diagram != null : "diagram != null";

        Element stateDataElement = new Element(XMLConstant.STATE_DATA
                .getIdentifier());
        for (SNLModel model : diagram.getChildren()) {
            if (model instanceof StateSetModel) {
                stateDataElement
                        .addContent(createStateSetTag((StateSetModel) model));
            } else if (model instanceof StateModel) {
                stateDataElement.addContent(createStateTag((StateModel) model));
            }
        }

        assert stateDataElement != null : "stateDataElement != null";
        return stateDataElement;
    }

    /**
     * Creates a XML element for a state.
     *
     * @param model
     *            The state to store
     * @return The corresponding element
     * @require model != null
     * @ensure stateElement != null
     */
    private Element createStateTag(StateModel model) {
        assert model != null : "model != null";

        Element stateElement = new Element(XMLConstant.STATE.getIdentifier());
        stateElement.setAttribute(XMLConstant.NAME.getIdentifier(), model
                .getPropertyValue(SNLModel.PARENT)
                + "." + model.getIdentifier());
        stateElement.setAttribute(XMLConstant.LOCATION_X.getIdentifier(),
                String.valueOf(model.getLocation().x));
        stateElement.setAttribute(XMLConstant.LOCATION_Y.getIdentifier(),
                String.valueOf(model.getLocation().y));
        stateElement.setAttribute(XMLConstant.WIDTH.getIdentifier(), String
                .valueOf(model.getSize().width));
        stateElement.setAttribute(XMLConstant.HEIGHT.getIdentifier(), String
                .valueOf(model.getSize().height));

        assert stateElement != null : "stateElement != null";
        return stateElement;
    }

    /**
     * Creates a XML element for a state-set.
     *
     * @param model
     *            The state-set to store
     * @return The corresponding element
     * @require model != null
     * @ensure stateSetElement != null
     */
    private Element createStateSetTag(StateSetModel model) {
        Element stateSetElement = new Element(XMLConstant.STATE_SET
                .getIdentifier());
        stateSetElement.setAttribute(XMLConstant.NAME.getIdentifier(), model
                .getIdentifier());
        stateSetElement.setAttribute(XMLConstant.LOCATION_X.getIdentifier(),
                String.valueOf(model.getLocation().x));
        stateSetElement.setAttribute(XMLConstant.LOCATION_Y.getIdentifier(),
                String.valueOf(model.getLocation().y));
        stateSetElement.setAttribute(XMLConstant.WIDTH.getIdentifier(), String
                .valueOf(model.getSize().width));
        stateSetElement.setAttribute(XMLConstant.HEIGHT.getIdentifier(), String
                .valueOf(model.getSize().height));
        return stateSetElement;
    }

    /**
     * {@inheritDoc}
     * @require originalFilePath != null
     */
    @Override
    public Map<String, List<Point>> loadConnectionLayoutData(
            IPath originalFilePath) {
        assert originalFilePath != null : "originalFilePath != null";
        Map<String, List<Point>> result = new HashMap<String, List<Point>>();
        ConnectionLayoutHandler handler = new ConnectionLayoutHandler(result);

        fillMap(originalFilePath, handler);

        return result;
    }

    /**
     * {@inheritDoc}
     * @require originalFilePath != null
     */
    @Override
    public Map<String, StateLayoutData> loadStateLayoutData(
            IPath originalFilePath) {
        assert originalFilePath != null : "originalFilePath != null";

        Map<String, StateLayoutData> result = new HashMap<String, StateLayoutData>();
        StateLayoutHandler handler = new StateLayoutHandler(result);

        fillMap(originalFilePath, handler);

        return result;
    }

    /**
     * Fills the given map with the loaded layout data.
     *
     * @param originalFilePath
     *            The path to the *.st file
     * @param handler
     *            The handler for the XML parser
     * @require originalFilePath != null
     * @require handler != null
     */
    private void fillMap(IPath originalFilePath, DefaultHandler handler) {
        assert originalFilePath != null : "originalFilePath != null";
        assert handler != null : "handler != null";

        IPath layoutDataPath = getLayoutDataPath(originalFilePath);
        File file = layoutDataPath.toFile();
        if (file.exists()) {
            try {
                SAXParser parser = SAXParserFactory.newInstance()
                        .newSAXParser();

                parser.parse(file, handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
