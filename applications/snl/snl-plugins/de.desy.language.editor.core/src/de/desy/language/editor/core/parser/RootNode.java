package de.desy.language.editor.core.parser;


public class RootNode extends Node {

    @Override
    public String humanReadableRepresentation() {
        return Messages.RootNode_ReturnMessage_ThisIsTheRoot;
    }

    @Override
    public String getNodeTypeName() {
        return Messages.RootNode_ReturnMessage_Root;
    }

}
