package org.csstudio.nams.configurator.treeviewer.treecomponents;


public class CategoryNode extends AbstractNode {
	
	private Categories _category;
	
	public CategoryNode(Categories category) {
		_category = category;
	}
	
	public Categories getCategory() {
		return _category;
	}

	@Override
	public String toString() {
		return _category.getDescription();
	}

}
