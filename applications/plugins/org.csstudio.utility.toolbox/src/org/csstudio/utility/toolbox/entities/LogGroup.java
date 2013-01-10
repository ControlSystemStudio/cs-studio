package org.csstudio.utility.toolbox.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Entity
@Table(name = "loggroup")
@NamedQueries({ @NamedQuery(name = LogGroup.FIND_ALL, query = "from LogGroup l order by l.groupName"),
			@NamedQuery(name = LogGroup.FIND_BY_EMAIL, query = "from LogGroup l where groupEmail = :groupEmail") })
public class LogGroup implements TextValue {

	public static final String FIND_ALL = "LogGroup.findAll";
	public static final String FIND_BY_EMAIL = "LogGroup.findByEmail";

	@Id
	@Column(name = "groupname")
	@ReadOnly
	private String groupName;

	@Column(name = "groupemail")
	private String groupEmail;

	@Column(name = "wartungemail")
	private String wartungEmail;

	@Column(name = "praefix")
	private String praefix;

	public static enum GroupGetValueProperty {
		GROUP_NAME, GROUP_EMAIL
	}

	@Transient
	private GroupGetValueProperty valueProperty = GroupGetValueProperty.GROUP_NAME;

	protected String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	protected String getGroupEmail() {
		return groupEmail;
	}

	protected String getWartungEmail() {
		return wartungEmail;
	}

	protected String getPraefix() {
		return praefix;
	}

	public void setValueProperty(GroupGetValueProperty valueProperty) {
		this.valueProperty = valueProperty;
	}

	@Override
	public String getValue() {
		if (valueProperty == GroupGetValueProperty.GROUP_NAME) {
			return groupName;
		} else if (valueProperty == GroupGetValueProperty.GROUP_EMAIL) {
			return groupEmail;
		} else {
			throw new IllegalStateException("Unknown valueProperty");
		}
	}

}
