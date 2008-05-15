package org.csstudio.nams.configurator.treeviewer.treecomponents;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode {
	
	private List<AbstractNode> _children;
	
	public AbstractNode() {
		_children = new ArrayList<AbstractNode>();
	}
	
	public AbstractNode[] getChildren() {
		return _children.toArray(new AbstractNode[_children.size()]);
	}
	
	public void addChild(AbstractNode child) {
		_children.add(child);
	}
	
	public void removeChild(AbstractNode child) {
		_children.remove(child);
	}

}
