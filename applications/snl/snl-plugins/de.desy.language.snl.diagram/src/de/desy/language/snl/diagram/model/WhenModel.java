package de.desy.language.snl.diagram.model;

public class WhenModel {

    private String _condition = "v>0";
    private String _destination = "state";

    public WhenModel(String condition, String destination) {
        _condition = condition;
        _destination = destination;
    }

    public String getCondition() {
        return _condition;
    }

    public String getDestination() {
        return _destination;
    }

    @Override
    public String toString() {
        return _condition+":"+_destination;
    }

}
