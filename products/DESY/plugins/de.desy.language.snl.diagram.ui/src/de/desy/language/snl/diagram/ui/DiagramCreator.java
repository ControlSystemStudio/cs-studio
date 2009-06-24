package de.desy.language.snl.diagram.ui;

import java.util.HashMap;
import java.util.Map;

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

	private static DiagramCreator _instance;
	
	private final Map<StateSetNode, HashMap<String, StateModel>> _stateSetMap;
	private final Map<WhenConnectionAnchors, Integer> _anchorMap;
	
	private final ConnectionBendPointCreator _bendPointCreator;
	
	private DiagramCreator() {
		_stateSetMap = new HashMap<StateSetNode, HashMap<String, StateModel>>();
		_anchorMap = new HashMap<WhenConnectionAnchors, Integer>();
		_bendPointCreator = new ConnectionBendPointCreator();
		_bendPointCreator.setSeparation(20);
	}
	
	public static DiagramCreator getInstance() {
		if (_instance == null) {
			_instance = new DiagramCreator();
		}
		return _instance;
	}

	public SNLDiagram createDefaultDiagram() {
		final SNLDiagram diagram = new SNLDiagram();
		return diagram;
	}

	public SNLDiagram createDiagram(final Node rootNode) {
		resetContainer();
		
		final SNLDiagram diagram = new SNLDiagram();
		addStateNodes(diagram, rootNode);
		addWhenNodes();
		return diagram;
	}
	
	private void resetContainer() {
		_stateSetMap.clear();
		_anchorMap.clear();
	}

	private void addStateNodes(final SNLDiagram diagram, final Node node) {
		for (final Node child : node.getChildrenNodes()) {
			if (child instanceof StateNode) {
				final StateSetNode parent = (StateSetNode) node;
				if (!_stateSetMap.containsKey(parent)) {
					_stateSetMap.put(parent, new HashMap<String, StateModel>());
				}
				final HashMap<String, StateModel> stateMap = _stateSetMap.get(parent);
				final StateNode stateNode = (StateNode) child;
				final StateModel state = new StateModel();
				final int size = stateMap.size();
				state.setLocation(new Point(50 + size * 200, 50 + (_stateSetMap
						.size() - 1) * 100));
				state.setStateNode(stateNode);
				state.setSize(new Dimension(100, 50));
				stateMap.put(stateNode.getSourceIdentifier(), state);
				diagram.addChild(state);
			} else {
				if (child.hasChildren()) {
					addStateNodes(diagram, child);
				}
			}
		}
	}

	private void addWhenNodes() {
		for (final HashMap<String, StateModel> map : _stateSetMap.values()) {
			for (final StateModel stateModel : map.values()) {
				final StateNode stateNode = stateModel.getStateNode();
				if (stateNode.hasChildren()) {
					for (final Node child : stateNode.getChildrenNodes()) {
						addWhenNodes(map, stateModel, child);
					}
				}
			}
		}
	}

	private void addWhenNodes(final HashMap<String, StateModel> map,
			final StateModel stateModel, final Node node) {
		if (node instanceof WhenNode) {
			final WhenNode when = (WhenNode) node;
			final String followingState = when.getFollowingState();
			final StateModel destination = map.get(followingState);
			if (destination != null && !destination.equals(stateModel)) {
				final WhenConnection whenCon = new WhenConnection(stateModel,
						destination);
				whenCon.setWhenNode(when);
				
				final WhenConnectionAnchors anchors = new WhenConnectionAnchors(stateModel, destination);
				final WhenConnectionAnchors reversAnchors = new WhenConnectionAnchors(destination, stateModel);
				
				if (_anchorMap.containsKey(anchors)) {
					int count = _anchorMap.get(anchors);
					count++;
					
					_bendPointCreator.create(whenCon, count);
					
					_anchorMap.put(anchors, new Integer(count));
				} else {
					_anchorMap.put(anchors, new Integer(0));
					_anchorMap.put(reversAnchors, new Integer(0));
				}
			}
		}
	}

}
