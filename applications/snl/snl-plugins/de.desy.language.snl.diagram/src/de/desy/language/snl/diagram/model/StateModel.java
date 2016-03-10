package de.desy.language.snl.diagram.model;

import de.desy.language.snl.parser.nodes.StateNode;

public class StateModel extends SNLModel {

    private static final long serialVersionUID = 1;

    public static final String STATE_NAME_PROP = "StateModel.Name";

    public static final String WHENS_PROP = "StateModel.Whens";

    private StateNode _stateNode;

    @Override
    public String getIconName() {
        return "ellipse16.gif";
    }

    @Override
    public String toString() {
        return "State '" + _stateNode.getSourceIdentifier() + "'";
    }

    public StateNode getStateNode() {
        return _stateNode;
    }

    public void setStateNode(StateNode stateNode) {
        _stateNode = stateNode;
    }

    @Override
    public String getIdentifier() {
        return _stateNode.getSourceIdentifier();
    }

    @Override
    protected boolean canHaveChildren() {
        return false;
    }

}
