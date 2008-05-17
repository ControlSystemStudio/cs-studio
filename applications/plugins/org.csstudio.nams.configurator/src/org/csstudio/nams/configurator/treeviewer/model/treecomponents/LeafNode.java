package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import org.csstudio.ams.configurationStoreService.util.TObject;
import org.csstudio.nams.configurator.Activator;

public class LeafNode<T extends TObject> extends CategoryNode {
	
	private T _content;

	public LeafNode(T content) {
		super(Categories.LEAF);
		_content = content;
	}
	
	@Override
	public void addChild(CategoryNode child) {
		Activator.getDefault().getLogger().logWarningMessage(this, "Tried to add a node to a leaf");
	}

	public T getContent() {
		return _content;
	}

}
