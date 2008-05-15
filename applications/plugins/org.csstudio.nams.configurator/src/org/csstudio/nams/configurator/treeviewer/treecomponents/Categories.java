package org.csstudio.nams.configurator.treeviewer.treecomponents;

import org.csstudio.ams.configurationStoreService.knownTObjects.FilterConditionTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.FilterTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.GroupsTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.TopicTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.UserGroupTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.UserTObject;
import org.csstudio.ams.configurationStoreService.util.TObject;

public enum Categories {
	
	ROOT("Root", null),
	USER("User", UserTObject.class),
	TOPIC("Topic", TopicTObject.class),
	USERGROUP("Usergroup", UserGroupTObject.class),
	FILTERCONDITION("Filter Condition", FilterConditionTObject.class),
 	FILTER("Filter", FilterTObject.class),
	GROUP("Group", GroupsTObject.class);
	
	private String _description;
	private Class<? extends TObject> _tObjectClass;

	private Categories(String description, Class<? extends TObject> tObjectClass) {
		_description = description;
		_tObjectClass = tObjectClass;
	}

	public Class<? extends TObject> getTObjectClass() {
		return _tObjectClass;
	}
	
	public String getDescription() {
		return _description;
	}

}
