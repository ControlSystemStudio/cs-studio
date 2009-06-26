package de.desy.language.snl.diagram.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import de.desy.language.editor.core.parser.Node;
import de.desy.language.snl.diagram.model.SNLDiagram;
import de.desy.language.snl.diagram.model.SNLElement;
import de.desy.language.snl.diagram.model.StateModel;
import de.desy.language.snl.diagram.model.StateSetModel;
import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.parser.nodes.StateNode;
import de.desy.language.snl.parser.nodes.StateSetNode;
import de.desy.language.snl.parser.nodes.WhenNode;

public class DiagramCreator {

	private static final int START_X = 50;
	private static final int START_Y = 50;
	private static final int DISTANCE_X = 300;
	private static final int DISTANCE_Y = 200;
	private static final int STATE_WIDTH = 100;
	private static final int STATE_HEIGHT = 50;
	private static final int STATESET_HEIGHT = 180;

	private static DiagramCreator _instance;

	private final Map<StateSetModel, HashMap<String, StateModel>> _stateSetMap;
	private final Map<WhenConnectionAnchors, Integer> _anchorMap;

	private final ConnectionBendPointCreator _bendPointCreator;
	private SNLDiagram _diagram;

	private DiagramCreator() {
		_stateSetMap = new HashMap<StateSetModel, HashMap<String, StateModel>>();
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

	public SNLDiagram createDiagram(final Node rootNode, int separation) {
		return this.createDiagram(rootNode, new HashMap<Node, List<Point>>(),
				separation);
	}

	public SNLDiagram createDiagram(final Node rootNode,
			Map<Node, List<Point>> storedCoordinates, int separation) {
		resetContainer();

		_bendPointCreator.setSeparation(separation);

		_diagram = new SNLDiagram();
		addStateNodes(_diagram, rootNode, storedCoordinates);
		addWhenNodes(storedCoordinates);
		return _diagram;
	}

	private void resetContainer() {
		_stateSetMap.clear();
		_anchorMap.clear();
	}

	private void addStateNodes(final SNLElement parentModel, final Node node,
			Map<Node, List<Point>> storedCoordinates) {
		for (final Node child : node.getChildrenNodes()) {
			if (child instanceof StateNode) {
				final HashMap<String, StateModel> stateMap = _stateSetMap
						.get(parentModel);
				final int size = stateMap.size();
				
				final StateNode stateNode = (StateNode) child;
				final StateModel state = new StateModel();

				List<Point> list = storedCoordinates.get(stateNode);
				if (list == null || list.isEmpty()) {
					int x = 2 * START_X + size * DISTANCE_X;
					int y = START_Y + (_stateSetMap.size() - 1) * DISTANCE_Y + (STATESET_HEIGHT - STATE_HEIGHT) / 2;
					state.setLocation(new Point(x, y ));
					System.out.println("\tState at "+x+"/"+y);
				} else {
					state.setLocation(list.get(0));
				}
				state.setSize(new Dimension(STATE_WIDTH, STATE_HEIGHT));
				state.setStateNode(stateNode);
				stateMap.put(stateNode.getSourceIdentifier(), state);
//				parentModel.addChild(state);
				_diagram.addChild(state);
			} else if (child instanceof StateSetNode) {
				StateSetModel setModel = new StateSetModel();
				setModel.setStateSetNode((StateSetNode) child);
				parentModel.addChild(setModel);
				
				if (!_stateSetMap.containsKey(setModel)) {
					_stateSetMap.put(setModel, new HashMap<String, StateModel>());
				}
				if (child.hasChildren()) {
					addStateNodes(setModel, child, storedCoordinates);
				}
				int childCount = _stateSetMap.get(setModel).size();
				int setWidth = 2 * START_X + (childCount-1) * DISTANCE_X + STATE_WIDTH;
				setModel.setSize(new Dimension(setWidth, STATESET_HEIGHT));
				int y = START_Y + (_stateSetMap.size() - 1) * DISTANCE_Y;
				System.out.println("StateSet at "+START_X+"/"+y);
				setModel.setLocation(new Point(START_X, y));
			} else {
				if (child.hasChildren()) {
					addStateNodes(parentModel, child, storedCoordinates);
				}
			}
		}
	}

	private void addWhenNodes(Map<Node, List<Point>> storedCoordinates) {
		for (final HashMap<String, StateModel> map : _stateSetMap.values()) {
			for (final StateModel stateModel : map.values()) {
				final StateNode stateNode = stateModel.getStateNode();
				if (stateNode.hasChildren()) {
					for (final Node child : stateNode.getChildrenNodes()) {
						addWhenNodes(map, stateModel, child, storedCoordinates);
					}
				}
			}
		}
	}

	private void addWhenNodes(final Map<String, StateModel> map,
			final StateModel stateModel, final Node node,
			Map<Node, List<Point>> storedCoordinates) {
		if (node instanceof WhenNode) {
			final WhenNode when = (WhenNode) node;
			final String followingState = when.getFollowingState();
			final StateModel destination = map.get(followingState);
			if (destination != null && !destination.equals(stateModel)) {
				final WhenConnection whenCon = new WhenConnection(stateModel,
						destination);
				whenCon.setWhenNode(when);

				List<Point> list = storedCoordinates.get(when);
				if (list == null) {
					final WhenConnectionAnchors anchors = new WhenConnectionAnchors(
							stateModel, destination);
					final WhenConnectionAnchors reversAnchors = new WhenConnectionAnchors(
							destination, stateModel);

					if (_anchorMap.containsKey(anchors)) {
						int count = _anchorMap.get(anchors);
						count++;

						_bendPointCreator.create(whenCon, count);

						_anchorMap.put(anchors, new Integer(count));
					} else {
						_anchorMap.put(anchors, new Integer(0));
						_anchorMap.put(reversAnchors, new Integer(0));
					}
				} else {
					int index = 0;
					for (Point bendPoint : list) {
						whenCon.addBendPoint(bendPoint, index);
						index++;
					}
				}
			}
		}
	}

}
