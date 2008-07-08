package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;


public class RubrikBean extends AbstractConfigurationBean<RubrikBean> {

	public static enum PropertyNames {
		groupId, rubrikName, rubrikType
	}

	private int groupId;	// PRIMARY KEY
	private String rubrikName;
	private RubrikTypeEnum rubrikType;

	public RubrikBean() {
		groupId = -1;
	}

	private int getGroupID() {
		return groupId;
	}

	private void setGroupID(int groupID) {
		int oldValue = getGroupID();
		this.groupId = groupID;
		pcs.firePropertyChange(
				PropertyNames.groupId.name(), oldValue,
				getGroupID());
	}

	public String getRubrikName() {
		return rubrikName;
	}

	public void setRubrikName(String rubrikName) {
		String oldValue = getRubrikName();
		this.rubrikName = rubrikName;
		pcs.firePropertyChange(PropertyNames.rubrikName
				.name(), oldValue, getRubrikName());
	}

	public RubrikTypeEnum getRubrikType() {
		return rubrikType;
	}

	public void setRubrikType(String rubrikType) {
		RubrikTypeEnum oldValue = getRubrikType();
		this.rubrikType = RubrikTypeEnum.valueOf(rubrikType);
		pcs.firePropertyChange(
				PropertyNames.rubrikType.name(), oldValue,
				getRubrikType());
	}

	public String getDisplayName() {
		return getRubrikName() != null ? getRubrikName() : "(ohne Namen)";
	}

	@Override
	protected void doUpdateState(RubrikBean bean) {
		setRubrikType(bean.getRubrikType().name());
		setRubrikName(bean.getRubrikName());
		setGroupID(bean.getGroupID());
	}

	public int getID() {
		return this.getGroupID();
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}

	public void setID(int id) {
		setGroupID(id);
	}
}
