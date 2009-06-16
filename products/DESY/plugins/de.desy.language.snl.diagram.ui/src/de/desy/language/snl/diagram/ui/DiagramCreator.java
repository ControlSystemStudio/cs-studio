package de.desy.language.snl.diagram.ui;

import java.util.HashMap;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import de.desy.language.editor.core.parser.Node;
import de.desy.language.snl.diagram.model.SNLDiagram;
import de.desy.language.snl.diagram.model.StateModel;
import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.parser.nodes.StateNode;
import de.desy.language.snl.parser.nodes.StateSetNode;
import de.desy.language.snl.parser.nodes.WhenNode;

public class DiagramCreator {

	private static HashMap<StateSetNode, HashMap<String, StateModel>> _stateSetMap;

	public static SNLDiagram createDefaultDiagram() {
		SNLDiagram diagram = new SNLDiagram();
		return diagram;
	}

	public static SNLDiagram createDiagram(Node rootNode) {
		SNLDiagram diagram = new SNLDiagram();
		_stateSetMap = new HashMap<StateSetNode, HashMap<String, StateModel>>();
		DiagramCreator.addStateNodes(diagram, rootNode);
		DiagramCreator.addWhenNodes();
		return diagram;
	}

	private static void addStateNodes(SNLDiagram diagram, Node node) {
		for (Node child : node.getChildrenNodes()) {
			if (child instanceof StateNode) {
				StateSetNode parent = (StateSetNode) node;
				if (!_stateSetMap.containsKey(parent)) {
					_stateSetMap.put(parent, new HashMap<String, StateModel>());
				}
				HashMap<String, StateModel> stateMap = _stateSetMap.get(parent);
				StateNode stateNode = (StateNode) child;
				StateModel state = new StateModel();
				int size = stateMap.size();
				state.setLocation(new Point(50 + size * 200, 50 + (_stateSetMap
						.size() - 1) * 100));
				state.setStateNode(stateNode);
				state.setSize(new Dimension(100, 50));
				stateMap.put(stateNode.getSourceIdentifier(), state);
				diagram.addChild(state);
			} else {
				if (child.hasChildren()) {
					DiagramCreator.addStateNodes(diagram, child);
				}
			}
		}
	}

	private static void addWhenNodes() {
		for (HashMap<String, StateModel> map : _stateSetMap.values()) {
			for (StateModel stateModel : map.values()) {
				StateNode stateNode = stateModel.getStateNode();
				if (stateNode.hasChildren()) {
					for (Node child : stateNode.getChildrenNodes()) {
						DiagramCreator.addWhenNodes(map, stateModel, child);
					}
				}
			}
		}
	}

	private static void addWhenNodes(HashMap<String, StateModel> map,
			StateModel stateModel, Node node) {
		if (node instanceof WhenNode) {
			WhenNode when = (WhenNode) node;
			String followingState = when.getFollowingState();
			StateModel destination = map.get(followingState);
			if (destination != null && !destination.equals(stateModel)) {
				WhenConnection whenCon = new WhenConnection(stateModel,
						destination);
				whenCon.setWhenNode(when);
			}
		}
	}

}
