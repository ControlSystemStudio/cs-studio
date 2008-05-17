package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CategoryNode {

	private Categories _category;
	
	private List<CategoryNode> _children;
	
	public CategoryNode(Categories category) {
		_children = new ArrayList<CategoryNode>();
		_category = category;
	}
	
	public CategoryNode[] getChildren() {
		return _children.toArray(new CategoryNode[_children.size()]);
	}
	
	public CategoryNode[] getChildrenWithCategory(Categories category) {
		List<CategoryNode> result = new LinkedList<CategoryNode>();
		for (CategoryNode node : _children) {
			if (node.getCategory().equals(category)) {
				result.add(node);
			}
		}
		return result.toArray(new CategoryNode[result.size()]);
	}
	
	public void addChild(CategoryNode child) {
		_children.add(child);
	}
	
	public void removeChild(CategoryNode child) {
		_children.remove(child);
	}

	public Categories getCategory() {
		return _category;
	}

	@Override
	public String toString() {
		return _category.getDescription();
	}

}
