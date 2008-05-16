package org.csstudio.nams.configurator.treeviewer.treecomponents;

import org.csstudio.ams.configurationStoreService.knownTObjects.UserTObject;

public class UserNode {
	
	private UserTObject _content;

	public UserNode(UserTObject content) {
		_content = content;
	}

	public UserTObject getContent() {
		return _content;
	}
	
	public String getDescription() {
		return _content.getName();
	}

}
