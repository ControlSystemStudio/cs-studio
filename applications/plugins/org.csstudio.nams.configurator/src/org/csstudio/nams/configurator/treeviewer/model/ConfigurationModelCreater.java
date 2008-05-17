package org.csstudio.nams.configurator.treeviewer.model;

import java.util.List;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.ams.configurationStoreService.knownTObjects.GroupsTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.UserTObject;
import org.csstudio.ams.service.logging.declaration.Logger;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.Categories;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.CategoryNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.GroupNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.GroupTypes;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.LeafNode;

public class ConfigurationModelCreater {

	private ConfigurationStoreService _storeService;
	private Logger _logger;

	public ConfigurationModelCreater(ConfigurationStoreService storeService,
			Logger logger) {
		_storeService = storeService;
		_logger = logger;
	}

	public CategoryNode createTreeModel() {
		CategoryNode root = new CategoryNode(Categories.ROOT);
		root.addChild(new CategoryNode(Categories.USER));
		root.addChild(new CategoryNode(Categories.TOPIC));
		root.addChild(new CategoryNode(Categories.USERGROUP));
		root.addChild(new CategoryNode(Categories.FILTERCONDITION));
		root.addChild(new CategoryNode(Categories.FILTER));
		// TODO add Database information here
		this.addGoups(root);
		this.addUser(root);
		return root;
	}

	private void addUser(CategoryNode root) {
		List<UserTObject> users = _storeService
				.getListOfConfigurations(UserTObject.class);
		CategoryNode[] userNodes = root
				.getChildrenWithCategory(Categories.USER);
		if (userNodes.length == 1) {
			for (UserTObject user : users) {
				LeafNode<UserTObject> newUserNode = new LeafNode<UserTObject>(
						user);
				CategoryNode parentForUser = this.getParentForUser(user,
						userNodes[0]);
				parentForUser.addChild(newUserNode);
			}
		} else {
			_logger.logWarningMessage(this,
					"Unexpected number of children: expected 1, but was "
							+ userNodes.length);
		}
	}

	private CategoryNode getParentForUser(UserTObject user,
			CategoryNode userNode) {
		CategoryNode[] categoryNodes = userNode
				.getChildrenWithCategory(Categories.GROUP);
		for (CategoryNode categoryNode : categoryNodes) {
			if (categoryNode instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) categoryNode;
				if (groupNode.getContent().getDatabaseId() == user
						.getGroupRef()) {
					return groupNode;
				}
			}
		}
		return userNode;
	}

	private void addGoups(CategoryNode root) {
		List<GroupsTObject> groups = _storeService
				.getListOfConfigurations(GroupsTObject.class);
		for (GroupsTObject group : groups) {
			GroupTypes groupType = GroupTypes.getGroupTypeForDbId(group
					.getType());
			if (groupType != null) {
				CategoryNode[] categoryNodes = root
						.getChildrenWithCategory(groupType
								.getAssociatedCategory());
				if (categoryNodes.length == 1) {
					CategoryNode groupNode = new GroupNode(group);
					categoryNodes[0].addChild(groupNode);
				} else {
					_logger.logWarningMessage(this,
							"Unexpected number of children: expected 1, but was "
									+ categoryNodes.length);
				}
			}

		}
	}

}
