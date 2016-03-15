package de.desy.language.snl.diagram.model;

import de.desy.language.snl.parser.nodes.StateSetNode;

/**
 * An elliptical shape.
 *
 */
public class StateSetModel extends SNLModel {

    private static final long serialVersionUID = 1;
    private StateSetNode _stateSetNode;

    @Override
    public String getIconName() {
        return "rectangle16.gif";
    }

    @Override
    public String toString() {
        return "StateSet '" + _stateSetNode.getSourceIdentifier() + "'";
    }

    @Override
    public String getIdentifier() {
        return _stateSetNode.getSourceIdentifier();
    }

    public void setStateSetNode(StateSetNode node) {
        _stateSetNode = node;
    }

    public StateSetNode getStateSetNode() {
        return _stateSetNode;
    }

    @Override
    protected boolean canHaveChildren() {
        return true;
    }

}
