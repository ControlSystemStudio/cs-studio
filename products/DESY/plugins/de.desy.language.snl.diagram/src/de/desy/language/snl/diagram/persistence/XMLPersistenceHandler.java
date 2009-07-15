package de.desy.language.snl.diagram.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class XMLPersistenceHandler implements IPersistenceHandler {

	public void store(IPath originalFilePath, SNLDiagram diagram) {
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		Element diagramElement = createDiagramTag(diagram);

		IPath layoutDataPath = getLayoutDataPath(originalFilePath);

		try {
			FileOutputStream outputStream = new FileOutputStream(layoutDataPath
					.toFile());
			outputter.output(diagramElement, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private IPath getLayoutDataPath(IPath originalFilePath) {
		String fileExtension = originalFilePath.getFileExtension();
		String fileName = originalFilePath.lastSegment();
		fileName = fileName.replace("." + fileExtension, ".layout");

		int segmentCount = originalFilePath.segmentCount();
		IPath constraintFilePath = originalFilePath
				.uptoSegment(segmentCount - 1);
		constraintFilePath = constraintFilePath.append(fileName);
		
		IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot()
		.getLocation();
		IPath layoutDataPath = workspacePath.append(constraintFilePath);
		return layoutDataPath;
	}

	private Element createDiagramTag(SNLDiagram diagram) {
		Element rootElement = new Element(XMLConstant.DIAGRAM.getIdentifier());
		rootElement.setAttribute(XMLConstant.NAME.getIdentifier(), diagram.getIdentifier());

		rootElement.addContent(createStateDataTag(diagram));
		rootElement.addContent(createConnectionDataTag(diagram));
		return rootElement;
	}

	private Element createConnectionDataTag(SNLDiagram diagram) {
		Element connectionDataElement = new Element(XMLConstant.CONNECTION_DATA.getIdentifier());
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
		return connectionDataElement;
	}

	private Element createConnectionTag(WhenConnection connection,
			String sourceIdentifier) {
		Element connectionElement = new Element(XMLConstant.CONNECTION.getIdentifier());
		connectionElement.setAttribute(XMLConstant.NAME.getIdentifier(), connection
				.getPropertyValue(SNLModel.PARENT)
				+ "."
				+ sourceIdentifier
				+ ".("
				+ connection.getIdentifier()
				+ ")");
		for (Point current : connection.getBendPoints()) {
			connectionElement.addContent(createPointTag(current));
		}
		return connectionElement;
	}

	private Element createPointTag(Point point) {
		Element pointElement = new Element(XMLConstant.POINT.getIdentifier());
		pointElement.setAttribute(XMLConstant.LOCATION_X.getIdentifier(), String.valueOf(point.x));
		pointElement.setAttribute(XMLConstant.LOCATION_Y.getIdentifier(), String.valueOf(point.y));
		return pointElement;
	}

	private Element createStateDataTag(SNLDiagram diagram) {
		Element stateDataElement = new Element(XMLConstant.STATE_DATA.getIdentifier());
		for (SNLModel model : diagram.getChildren()) {
			if (model instanceof StateSetModel) {
				stateDataElement
						.addContent(createStateSetTag((StateSetModel) model));
			} else if (model instanceof StateModel) {
				stateDataElement.addContent(createStateTag((StateModel) model));
			}
		}
		return stateDataElement;
	}

	private Element createStateTag(StateModel model) {
		Element stateElement = new Element(XMLConstant.STATE.getIdentifier());
		stateElement.setAttribute(XMLConstant.NAME.getIdentifier(), model
				.getPropertyValue(SNLModel.PARENT)
				+ "." + model.getIdentifier());
		stateElement.setAttribute(XMLConstant.LOCATION_X.getIdentifier(), String.valueOf(model
				.getLocation().x));
		stateElement.setAttribute(XMLConstant.LOCATION_Y.getIdentifier(), String.valueOf(model
				.getLocation().y));
		stateElement.setAttribute(XMLConstant.WIDTH.getIdentifier(), String
				.valueOf(model.getSize().width));
		stateElement.setAttribute(XMLConstant.HEIGHT.getIdentifier(), String
				.valueOf(model.getSize().height));
		return stateElement;
	}

	private Element createStateSetTag(StateSetModel model) {
		Element stateSetElement = new Element(XMLConstant.STATE_SET.getIdentifier());
		stateSetElement.setAttribute(XMLConstant.NAME.getIdentifier(), model.getIdentifier());
		stateSetElement.setAttribute(XMLConstant.LOCATION_X.getIdentifier(), String.valueOf(model
				.getLocation().x));
		stateSetElement.setAttribute(XMLConstant.LOCATION_Y.getIdentifier(), String.valueOf(model
				.getLocation().y));
		stateSetElement.setAttribute(XMLConstant.WIDTH.getIdentifier(), String
				.valueOf(model.getSize().width));
		stateSetElement.setAttribute(XMLConstant.HEIGHT.getIdentifier(), String
				.valueOf(model.getSize().height));
		return stateSetElement;
	}

	public Map<String, List<Point>> loadConnectionLayoutData(
			IPath originalFilePath) {
		Map<String, List<Point>> result = new HashMap<String, List<Point>>();
		ConnectionLayoutHandler handler = new ConnectionLayoutHandler(result);
		
		fillMap(originalFilePath, handler);

		return result;
	}

	public Map<String, StateLayoutData> loadStateLayoutData(
			IPath originalFilePath) {
		Map<String, StateLayoutData> result = new HashMap<String, StateLayoutData>();
		StateLayoutHandler handler = new StateLayoutHandler(result);
		
		fillMap(originalFilePath, handler);
		
		return result;
	}

	private void fillMap(IPath originalFilePath, DefaultHandler handler) {
		IPath layoutDataPath = getLayoutDataPath(originalFilePath);
		File file = layoutDataPath.toFile();
		if (file.exists()) {
			try {
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				
				parser.parse(file, handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
