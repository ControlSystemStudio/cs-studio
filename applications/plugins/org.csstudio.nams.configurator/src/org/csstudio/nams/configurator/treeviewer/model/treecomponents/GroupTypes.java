package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

public enum GroupTypes {
	
	USERS(1, Categories.USER),
	TOPICS(5, Categories.TOPIC),
	USERGROUPS(2, Categories.USERGROUP),
	FILTERCONDITIONS(3, Categories.FILTERCONDITION),
	FILTER(4, Categories.FILTER);
	
	private int _dbGroupType;
	private Categories _associatedCategory;

	private GroupTypes(int dBgroupType, Categories associatedCategory) {
		_dbGroupType = dBgroupType;
		_associatedCategory = associatedCategory;
	}

	public int getDbGroupType() {
		return _dbGroupType;
	}
	
	public Categories getAssociatedCategory() {
		return _associatedCategory;
	}
	
	public static GroupTypes getGroupTypeForDbId(int dbGroupType) {
		for (GroupTypes type : GroupTypes.values()) {
			if (type.getDbGroupType()==dbGroupType) {
				return type;
			}
		}
		return null;
	}

}
