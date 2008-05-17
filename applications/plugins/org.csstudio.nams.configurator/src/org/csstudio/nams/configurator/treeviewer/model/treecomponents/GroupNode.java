package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import org.csstudio.ams.configurationStoreService.knownTObjects.GroupsTObject;

public class GroupNode extends CategoryNode {

	private GroupsTObject _content;

	public GroupNode(GroupsTObject content) {
		super(Categories.GROUP);
		_content = content;
	}

	public GroupsTObject getContent() {
		return _content;
	}
	
	@Override
	public String toString() {
		return _content.getName();
	}

}
